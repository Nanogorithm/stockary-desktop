package com.stockary.common.ui.product

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.product.ProductRepository
import kotlinx.coroutines.flow.map

class ProductInputHandler(
    val productRepository: ProductRepository
) : InputHandler<ProductContract.Inputs, ProductContract.Events, ProductContract.State> {
    override suspend fun InputHandlerScope<ProductContract.Inputs, ProductContract.Events, ProductContract.State>.handleInput(
        input: ProductContract.Inputs
    ) = when (input) {
        is ProductContract.Inputs.Initialize -> {
            postInput(ProductContract.Inputs.FetchProductList(forceRefresh = true))
        }

        is ProductContract.Inputs.GoBack -> {
            postEvent(ProductContract.Events.NavigateUp)
        }

        is ProductContract.Inputs.FetchProductList -> {
            observeFlows("FetchProductList") {
                listOf(productRepository.getProductList(refreshCache = input.forceRefresh)
                    .map { ProductContract.Inputs.UpdateProductList(it) })
            }
        }

        is ProductContract.Inputs.UpdateProductList -> {
            updateState { it.copy(products = input.products) }
        }

        ProductContract.Inputs.GoAddNew -> {
            postEvent(ProductContract.Events.GoProductAdd)
        }

        ProductContract.Inputs.GoCategory -> {
            postEvent(ProductContract.Events.GoCategories)
        }

        is ProductContract.Inputs.Delete -> {
            updateState { it.copy(deleteResponse = SupabaseResource.Loading) }
            observeFlows("DeleteProduct") {
                listOf(productRepository.delete(product = input.product)
                    .map { ProductContract.Inputs.UpdateDeleteResponse(it) })
            }
        }

        is ProductContract.Inputs.UpdateDeleteResponse -> {
            updateState { it.copy(deleteResponse = input.response) }
        }

        is ProductContract.Inputs.GoEdit -> postEvent(ProductContract.Events.GoProductEdit(input.productId))
    }
}
