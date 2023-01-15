package com.stockary.common.ui.new_product

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.product.ProductRepository
import com.stockary.common.repository.product.model.Product
import com.stockary.common.ui.new_category.NewCategoryContract
import kotlinx.coroutines.flow.map

class NewProductInputHandler(
    val productRepository: ProductRepository
) : InputHandler<
        NewProductContract.Inputs,
        NewProductContract.Events,
        NewProductContract.State> {
    override suspend fun InputHandlerScope<
            NewProductContract.Inputs,
            NewProductContract.Events,
            NewProductContract.State>.handleInput(
        input: NewProductContract.Inputs
    ) = when (input) {
        is NewProductContract.Inputs.Initialize -> {

        }

        is NewProductContract.Inputs.GoBack -> {
            postEvent(NewProductContract.Events.NavigateUp)
        }

        is NewProductContract.Inputs.Save -> {
            updateState { it.copy(response = SupabaseResource.Loading) }
            observeFlows("SavingNewCategory") {
                listOf()
//                listOf(productRepository.add(product = Product()).map {
//                    NewProductContract.Inputs.UpdateSaveResponse(it)
//                })
            }
        }

        is NewProductContract.Inputs.SaveAndContinue -> {

        }

        is NewProductContract.Inputs.UpdateSaveResponse -> {

        }
    }
}
