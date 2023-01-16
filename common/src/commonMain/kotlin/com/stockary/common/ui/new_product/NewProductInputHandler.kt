package com.stockary.common.ui.new_product

import androidx.compose.ui.text.input.TextFieldValue
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.CategoryRepository
import com.stockary.common.repository.product.ProductRepository
import com.stockary.common.repository.product.model.Product
import kotlinx.coroutines.flow.map

class NewProductInputHandler(
    val productRepository: ProductRepository, val categoryRepository: CategoryRepository
) : InputHandler<NewProductContract.Inputs, NewProductContract.Events, NewProductContract.State> {
    override suspend fun InputHandlerScope<NewProductContract.Inputs, NewProductContract.Events, NewProductContract.State>.handleInput(
        input: NewProductContract.Inputs
    ) = when (input) {
        is NewProductContract.Inputs.Initialize -> {
            postInput(NewProductContract.Inputs.FetchCustomerTypes(true))
            postInput(NewProductContract.Inputs.FetchCategories(true))
        }

        is NewProductContract.Inputs.GoBack -> {
            postEvent(NewProductContract.Events.NavigateUp)
        }

        is NewProductContract.Inputs.Save -> {
            updateState { it.copy(response = SupabaseResource.Loading) }
            observeFlows("PublishNewProduct") {
                listOf()
//                listOf(productRepository.add(product = Product()).map {
//                    NewProductContract.Inputs.UpdateSaveResponse(it)
//                })
            }
        }

        is NewProductContract.Inputs.SaveAndContinue -> {
            val currentState = getCurrentState()
            updateState { it.copy(response = SupabaseResource.Loading) }
            observeFlows("SavingNewProduct") {
                listOf(productRepository.add(
                    product = Product(
                        title = currentState.productName.text,
                        description = currentState.productDescription.text,
                        price = currentState.basePrice.text.toFloat(),
                        categoryId = input.category.id,
                        stock = 1
                    ), prices = input.prices, types = input.types
                ).map { NewProductContract.Inputs.UpdateSaveResponse(it) })
            }
        }

        is NewProductContract.Inputs.UpdateSaveResponse -> {
            if (input.response is SupabaseResource.Success) {
                postEvent(NewProductContract.Events.NavigateUp)
            }
            updateState { it.copy(response = input.response) }
        }

        is NewProductContract.Inputs.UpdateCategories -> {
            updateState { it.copy(categoryList = input.categoryList) }
        }

        is NewProductContract.Inputs.FetchCategories -> {
            observeFlows("FetchCategories") {
                listOf(categoryRepository.getCategoryList(input.forceRefresh)
                    .map { NewProductContract.Inputs.UpdateCategories(it) })
            }
        }

        is NewProductContract.Inputs.FetchCustomerTypes -> {
            observeFlows("FetchCustomerTypes") {
                listOf(productRepository.getCustomerTypes(input.forceRefresh)
                    .map { NewProductContract.Inputs.UpdateCustomerTypes(it) })
            }
        }

        is NewProductContract.Inputs.UpdateCustomerTypes -> {
            val size = input.customerTypes.getCachedOrEmptyList()
            updateState { it.copy(customerType = input.customerTypes) }
            val prices = mutableMapOf<String, TextFieldValue>().apply {
                size.forEach {
                    it.name to TextFieldValue()
                }
            }
            updateState { it.copy(prices = prices) }
        }

        is NewProductContract.Inputs.NameChanged -> {
            updateState { it.copy(productName = input.newValue) }
        }

        is NewProductContract.Inputs.PriceChanged -> {
            updateState { it.copy(basePrice = input.newValue) }
        }

        is NewProductContract.Inputs.DescriptionChanged -> {
            updateState { it.copy(productDescription = input.newValue) }
        }

        is NewProductContract.Inputs.PricesChanged -> {
            val currentState = getCurrentState().prices
            val prices = mutableMapOf<String, TextFieldValue>().apply {
                currentState.forEach {
                    if (it.key == input.role.name) {
                        it.key to input.newValue
                    } else {
                        it.key to it.value
                    }
                }
            }
            updateState { it.copy(prices = prices) }
        }
    }
}
