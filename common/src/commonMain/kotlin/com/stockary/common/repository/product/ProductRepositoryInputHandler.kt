package com.stockary.common.repository.product

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.google.cloud.firestore.SetOptions
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.cloud.StorageClient
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Media
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType
import io.github.jan.supabase.storage.storage
import org.koin.core.component.KoinComponent
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL


class ProductRepositoryInputHandler(
    private val eventBus: EventBus,
) : InputHandler<ProductRepositoryContract.Inputs, Any, ProductRepositoryContract.State>, KoinComponent {

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
                postInput(ProductRepositoryContract.Inputs.RefreshProductList(true))
            }
            if (currentState.customerTypesInitialized) {
                postInput(ProductRepositoryContract.Inputs.RefreshCustomerTypes(true))
            }

            Unit
        }

        is ProductRepositoryContract.Inputs.ProductListUpdated -> {
            updateState { it.copy(productList = input.dataList) }
        }

        is ProductRepositoryContract.Inputs.RefreshProductList -> {
            updateState { it.copy(dataListInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.productList },
                updateState = { ProductRepositoryContract.Inputs.ProductListUpdated(it) },
                doFetch = {
                    val firestore = FirestoreClient.getFirestore()

                    val future = firestore.collection("products").get()
                    val data = future.get()

                    data.documents.mapNotNull { docSnap ->
                        try {
                            val product = docSnap.toObject(Product::class.java)
                            product?.apply {
                                id = docSnap.id
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is ProductRepositoryContract.Inputs.Add -> {
            try {
                val firestore = FirestoreClient.getFirestore()

                val prices = mutableMapOf<String, Any>().apply {
                    input.types.forEachIndexed { index, role ->
                        put(role.slug!!, input.prices[index])
                    }
                }

                val units = mapOf(
                    "amount" to input.product.unitAmount, "type" to input.product.unitType
                )

                println("saving => $prices $units")

                val product = firestore.collection("products").add(
                    mapOf(
                        "title" to input.product.title,
                        "code" to input.product.code,
                        "category" to input.product.category,
                        "media" to input.media,
                        "prices" to prices,
                        "units" to units
                    )
                )

                updateState { it.copy(saving = SupabaseResource.Success(true)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(saving = SupabaseResource.Error(e)) }
            }
        }

        is ProductRepositoryContract.Inputs.Delete -> {
            try {
                val firestore = FirestoreClient.getFirestore()
                val delete = firestore.collection("products").document(input.product.id!!).delete()

                updateState { it.copy(deleting = SupabaseResource.Success(true)) }
                postInput(ProductRepositoryContract.Inputs.RefreshProductList(true))
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(deleting = SupabaseResource.Error(e)) }
            }
        }

        is ProductRepositoryContract.Inputs.Edit -> {
            try {
                val firestore = FirestoreClient.getFirestore()

                val prices = mutableMapOf<String, Any>().apply {
                    input.types.forEachIndexed { index, role ->
                        put(role.slug!!, input.prices[index])
                    }
                }

                val units = mapOf(
                    "amount" to input.updated.unitAmount, "type" to input.updated.unitType
                )

                println("updating => $prices $units")

                val product = firestore.collection("products").document(input.product.id!!).set(
                    mapOf(
                        "title" to input.updated.title,
                        "code" to input.updated.code,
                        "category" to input.updated.category,
                        "media" to input.photo,
                        "prices" to prices,
                        "units" to units
                    ), SetOptions.merge()
                )

                updateState { it.copy(saving = SupabaseResource.Success(true)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(saving = SupabaseResource.Error(e)) }
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
                    val firestore = FirestoreClient.getFirestore()
                    val future = firestore.collection("types").get()
                    val data = future.get()

                    data.documents.mapNotNull { docSnap ->
                        try {
                            val role = docSnap.toObject(Role::class.java)
                            role?.apply {
                                id = docSnap.id
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is ProductRepositoryContract.Inputs.UpdateCustomerTypes -> {
            updateState { it.copy(customerTypes = input.roles) }
        }

        is ProductRepositoryContract.Inputs.RefreshUnitTypes -> {
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.unitTypes },
                updateState = { ProductRepositoryContract.Inputs.UpdateUnitTypes(it) },
                doFetch = {
                    val firestore = FirestoreClient.getFirestore()
                    val future = firestore.collection("unit_types").get()
                    val data = future.get()

                    data.documents.mapNotNull { docSnap ->
                        try {
                            val role = docSnap.toObject(UnitType::class.java)
                            role?.apply {
                                id = docSnap.id
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is ProductRepositoryContract.Inputs.UpdateUnitTypes -> {
            updateState { it.copy(unitTypes = input.unitTypes) }
        }

        is ProductRepositoryContract.Inputs.UpdateUploadResponse -> {
            updateState { it.copy(mediaUploadResponse = input.mediaUploadResponse) }
        }

        is ProductRepositoryContract.Inputs.UploadPhoto -> {
            updateState { it.copy(mediaUploadResponse = SupabaseResource.Loading) }

            //upload to firebase storage
            val bucket: Bucket? = StorageClient.getInstance().bucket()
            bucket?.let {
                val objectName = "products/${System.currentTimeMillis()}.${input.file.extension}"
                val contentType = getMimeType(input.file.toURI().toURL())
                val fileBlob = if (contentType != null && contentType != "") {
                    val blob = it.create(
                        objectName,
                        input.file.inputStream(),
                        contentType,
                        Bucket.BlobWriteOption.doesNotExist(),
                        Bucket.BlobWriteOption.predefinedAcl(
                            Storage.PredefinedAcl.PUBLIC_READ
                        )
                    )
//                    println("metadataLink => ${blob.metadata["mediaLink"]}, ${blob.mediaLink}")
                    blob
                } else {
                    val blob = it.create(
                        objectName,
                        input.file.inputStream(),
                        Bucket.BlobWriteOption.doesNotExist(),
                        Bucket.BlobWriteOption.predefinedAcl(
                            Storage.PredefinedAcl.PUBLIC_READ
                        )
                    )
//                    println("metadataLink => ${blob.metadata["mediaLink"]}, ${blob.mediaLink}")
                    blob
                }

                updateState {
                    it.copy(
                        mediaUploadResponse = SupabaseResource.Success(
                            Media(
                                path = objectName, url = fileBlob.mediaLink
                            )
                        )
                    )
                }
            }

            Unit
        }

        is ProductRepositoryContract.Inputs.GetProduct -> {
            try {
                val firestore = FirestoreClient.getFirestore()
                val data = firestore.collection("products").document(input.productId).get().get()
                val product = data.toObject(Product::class.java)

                product!!.apply {
                    id = data.id
                }
                updateState { it.copy(product = SupabaseResource.Success(product)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(product = SupabaseResource.Error(e)) }
            }
        }

        is ProductRepositoryContract.Inputs.GetPhotoUrl -> {

        }
    }
}

@Throws(IOException::class, MalformedURLException::class)
fun getMimeType(fileUrl: URL): String? {
    val uc = fileUrl.openConnection()
    return uc.contentType
}