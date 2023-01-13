package com.stockary.common.ui.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.product.model.Product

object ProductContract {
    data class State(
        val loading: Boolean = false,
        val productListInitialized: Boolean = false,
        val products: Cached<List<Product>> = Cached.NotLoaded()
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        data class FetchProductList(val forceRefresh: Boolean) : Inputs()
        data class UpdateProductList(val products: Cached<List<Product>>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
