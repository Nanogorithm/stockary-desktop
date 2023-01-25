package com.stockary.common.ui.new_product

import androidx.compose.ui.text.input.TextFieldValue
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.*
import com.stockary.common.repository.category.CategoryRepository
import com.stockary.common.repository.product.ProductRepository
import com.stockary.common.repository.product.model.Product
import com.stockary.common.storagePrefix
import kotlinx.coroutines.flow.map

class NewProductInputHandler(
    val productRepository: ProductRepository, val categoryRepository: CategoryRepository
) : InputHandler<NewProductContract.Inputs, NewProductContract.Events, NewProductContract.State> {
    override suspend fun InputHandlerScope<NewProductContract.Inputs, NewProductContract.Events, NewProductContract.State>.handleInput(
        input: NewProductContract.Inputs
    ) = when (input) {
        is NewProductContract.Inputs.Initialize -> {
            updateState { it.copy(productId = input.productId) }
            postInput(NewProductContract.Inputs.FetchCustomerTypes(true))
            postInput(NewProductContract.Inputs.FetchCategories(true))
            postInput(NewProductContract.Inputs.FetchUnitTypes(true))
            input.productId?.let {
                postInput(NewProductContract.Inputs.GetProduct(it))
            }
            Unit
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

            if (currentState.uploadResponse is SupabaseResource.Success) {
                val photoPath = currentState.uploadResponse.data
                val photoField: TextFieldState = currentState.formState.getState("photo")
                photoField.change(photoPath)
            }

            val formData: Product = currentState.formState.getData()
            val rawData = currentState.formState.getMap()

            observeFlows("SavingNewProduct") {
                listOf(productRepository.add(
                    product = formData,
                    prices = currentState.customerType.getCachedOrEmptyList().map { rawData[it.name] as Float },
                    types = currentState.customerType.getCachedOrEmptyList()
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
            postInput(NewProductContract.Inputs.UpdateForm)
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
            val types = input.customerTypes.getCachedOrEmptyList()
            val names = types.map { it.name }
            val currentState = getCurrentState()
            updateState {
                it.copy(
                    customerType = input.customerTypes, formState = FormState(mutableListOf<BaseState<*>>().apply {
                        addAll(currentState.formState.fields)
                        val fields = currentState.formState.fields.map {
                            it.name
                        }
                        names.forEach {
                            if (!fields.contains(it)) {
                                add(TextFieldState(name = it, transform = { it.toFloatOrNull() ?: 0f }))
                            }
                        }
                    })
                )
            }

            postInput(NewProductContract.Inputs.UpdateForm)
        }

        is NewProductContract.Inputs.FetchUnitTypes -> {
            observeFlows("FetchUnitTypes") {
                listOf(productRepository.getProductUnitTypes(input.forceRefresh)
                    .map { NewProductContract.Inputs.UpdateUnitTypes(it) })
            }
        }

        is NewProductContract.Inputs.UpdateUnitTypes -> {
            updateState { it.copy(unitTypes = input.unitTypes) }
            postInput(NewProductContract.Inputs.UpdateForm)
        }

        is NewProductContract.Inputs.UpdateUploadResponse -> {
            updateState { it.copy(uploadResponse = input.uploadResponse) }
        }

        is NewProductContract.Inputs.UploadPhoto -> {
            observeFlows("UploadPhoto") {
                listOf(
                    productRepository.uploadPhoto(input.file)
                        .map { NewProductContract.Inputs.UpdateUploadResponse(it) })
            }
        }

        is NewProductContract.Inputs.GetProduct -> {
            updateState { it.copy(product = SupabaseResource.Loading) }
            observeFlows("GetProduct") {
                listOf(
                    productRepository.get(input.productId)
                        .map { NewProductContract.Inputs.UpdateProduct(it) }
                )
            }
        }

        is NewProductContract.Inputs.UpdateProduct -> {
            updateState { it.copy(product = input.product) }
            postInput(NewProductContract.Inputs.UpdateForm)
        }

        NewProductContract.Inputs.UpdateForm -> {
            val currentState = getCurrentState()

            if (currentState.product is SupabaseResource.Success) {
                val formState = currentState.formState
                val product: Product = currentState.product.data

                formState.getState<TextFieldState>("title").change(product.title)
                formState.getState<TextFieldState>("unit_amount").change(product.unitAmount.toString())
                formState.getState<TextFieldState>("description").change(product.description ?: "")
                formState.getState<TextFieldState>("photo").change(product.photo?.let { "${storagePrefix}${it}" } ?: "")
                formState.getState<ChoiceState>("unit_type_id").change(product.unitTypeId.toString())
                formState.getState<ChoiceState>("category_id").change(product.categoryId.toString())

                //set prices
                currentState.customerType.getCachedOrEmptyList().forEach { _role ->
                    product.productCustomerRole.firstOrNull { it.customer_role_id == _role.id }?.let {
                        formState.getState<TextFieldState>(_role.name).change(it.price.toString())
                    }
                }
            }
            Unit
        }

        NewProductContract.Inputs.Update -> {
            val currentState = getCurrentState()
            updateState { it.copy(response = SupabaseResource.Loading) }
            if (currentState.product is SupabaseResource.Success) {
                val product: Product = currentState.product.data
                val updated: Product = currentState.formState.getData()
                val rawData = currentState.formState.getMap()

                val prices = currentState.customerType.getCachedOrEmptyList().map { rawData[it.name] as Float }
                val types = currentState.customerType.getCachedOrEmptyList()

                observeFlows("Update") {
                    listOf(
                        productRepository.edit(product = product, updated = updated, prices = prices, types = types)
                            .map { NewProductContract.Inputs.UpdateSaveResponse(it) }
                    )
                }

            }
            Unit
        }
    }
}
