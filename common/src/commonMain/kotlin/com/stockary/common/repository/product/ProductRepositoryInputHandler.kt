package com.stockary.common.repository.product

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.ProductCustomerRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductRepositoryInputHandler(
    private val eventBus: EventBus,
) : InputHandler<ProductRepositoryContract.Inputs, Any, ProductRepositoryContract.State>, KoinComponent {

    val supabaseClient: SupabaseClient by inject()

    override suspend fun InputHandlerScope<ProductRepositoryContract.Inputs, Any, ProductRepositoryContract.State>.handleInput(
        input: ProductRepositoryContract.Inputs
    ) = when (input) {
        is ProductRepositoryContract.Inputs.ClearCaches -> {
            updateState { ProductRepositoryContract.State() }
        }

        is ProductRepositoryContract.Inputs.Initialize -> {
            val previousState = getCurrentState()
            if (!previousState.initialized) {
                updateState { it.copy(initialized = true) }
                // start observing flows here
                logger.debug("initializing")
                observeFlows(
                    key = "Observe account changes",
                    eventBus.observeInputsFromBus<ProductRepositoryContract.Inputs>(),
                )
            } else {
                logger.debug("already initialized")
                noOp()
            }
        }

        is ProductRepositoryContract.Inputs.RefreshAllCaches -> {
            // then refresh all the caches in this repository
            val currentState = getCurrentState()
            if (currentState.dataListInitialized) {
                postInput(ProductRepositoryContract.Inputs.RefreshDataList(true))
            }
            if (currentState.customerTypesInitialized) {
                postInput(ProductRepositoryContract.Inputs.RefreshCustomerTypes(true))
            }

            Unit
        }

        is ProductRepositoryContract.Inputs.DataListUpdated -> {
            updateState { it.copy(dataList = input.dataList) }
        }

        is ProductRepositoryContract.Inputs.RefreshDataList -> {
            updateState { it.copy(dataListInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.dataList },
                updateState = { ProductRepositoryContract.Inputs.DataListUpdated(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["products"].select("*,categories(*),unit_types(*)")
                    println("products => ${result.body}")
                    result.decodeList(json = Json {
                        ignoreUnknownKeys = true
                    })
                },
            )
        }

        is ProductRepositoryContract.Inputs.Add -> {
            try {
                val result = supabaseClient.postgrest["products"].insert(input.product)
                //create prices
                val product = result.decodeSingle<Product>(json = Json { ignoreUnknownKeys = true })
                val prices = mutableListOf<ProductCustomerRole>().apply {
                    input.prices.forEachIndexed { index, price ->
                        add(
                            ProductCustomerRole(
                                product_id = product.id!!, customer_role_id = input.types[index].id, price = price
                            )
                        )
                    }
                }
                if (prices.isNotEmpty()) {
                    supabaseClient.postgrest["product_customer_roles"].insert(
                        prices
                    )
                }
                updateState { it.copy(saving = SupabaseResource.Success(true)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(saving = SupabaseResource.Error(e)) }
            }
        }

        is ProductRepositoryContract.Inputs.Delete -> {
            try {
                val result = supabaseClient.postgrest["products"].delete { Product::id eq input.product.id }
                println("product delete => ${result.body}")
                updateState { it.copy(deleting = SupabaseResource.Success(true)) }
                postInput(ProductRepositoryContract.Inputs.RefreshDataList(true))
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(deleting = SupabaseResource.Error(e)) }
            }
        }

        is ProductRepositoryContract.Inputs.Edit -> {
            try {
                println("${input.product} ${input.updated}")
                val result = supabaseClient.postgrest["products"].update({
                    if (input.product.title != input.updated.title) {
                        Product::title setTo input.updated.title
                    }
                    if (input.product.description != input.updated.description) {
                        Product::description setTo input.updated.description
                    }

                    if (input.product.photo != input.updated.photo) {
                        Product::photo setTo input.updated.photo
                    }

                    if (input.product.categoryId != input.updated.categoryId) {
                        Product::categoryId setTo input.updated.categoryId
                    }

                    if (input.product.unitAmount != input.updated.unitAmount) {
                        Product::unitAmount setTo input.updated.unitAmount
                    }
                    if (input.product.unitTypeId != input.updated.unitTypeId) {
                        Product::unitTypeId setTo input.updated.unitTypeId
                    }
                }) {
                    Product::id eq input.product.id
                }

                updateState { it.copy(updating = SupabaseResource.Success(true)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(updating = SupabaseResource.Error(e)) }
            }
        }

        is ProductRepositoryContract.Inputs.RefreshCustomerTypes -> {
            updateState { it.copy(customerTypesInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.customerTypes },
                updateState = { ProductRepositoryContract.Inputs.UpdateCustomerTypes(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["customer_roles"].select("*")
                    println("Customer types => ${result.body}")
                    result.decodeList(json = Json {
                        ignoreUnknownKeys = true
                    })
                },
            )
        }

        is ProductRepositoryContract.Inputs.UpdateCustomerTypes -> {
            updateState { it.copy(customerTypes = input.dataList) }
        }

        is ProductRepositoryContract.Inputs.RefreshUnitTypes -> {
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.unitTypes },
                updateState = { ProductRepositoryContract.Inputs.UpdateUnitTypes(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["unit_types"].select("*")
                    println("Unit types => ${result.body}")
                    result.decodeList(json = Json {
                        ignoreUnknownKeys = true
                    })
                },
            )
        }

        is ProductRepositoryContract.Inputs.UpdateUnitTypes -> {
            updateState { it.copy(unitTypes = input.unitTypes) }
        }

        is ProductRepositoryContract.Inputs.UpdateUploadResponse -> {
            updateState { it.copy(photoUploadResponse = input.photoUploadResponse) }
        }

        is ProductRepositoryContract.Inputs.UploadPhoto -> {
            updateState { it.copy(photoUploadResponse = SupabaseResource.Loading) }
            val result = supabaseClient.storage["shad"].upload(
                "products/${System.currentTimeMillis()}.${input.file.extension}", input.file.readBytes()
            )
            println("upload path => $result")
            updateState { it.copy(photoUploadResponse = SupabaseResource.Success(result)) }
        }

        is ProductRepositoryContract.Inputs.GetProduct -> {
            try {
                val result = supabaseClient.postgrest["products"].select("*,product_customer_roles(*)") {
                    Product::id eq input.productId
                }
                println("get ${result.body}")
                val product: Product = result.decodeSingle(json = Json {
                    ignoreUnknownKeys = true
                })
                updateState { it.copy(product = SupabaseResource.Success(product)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(product = SupabaseResource.Error(e)) }
            }
        }

        is ProductRepositoryContract.Inputs.GetPhotoUrl -> {
            val result = supabaseClient.storage["shad"].publicRenderUrl(input.path)
            println("imagePath $result")
        }
    }
}
