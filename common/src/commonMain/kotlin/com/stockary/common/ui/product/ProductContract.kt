package com.stockary.common.ui.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.product.model.Product

object ProductContract {
    data class State(
        val loading: Boolean = false,
        val productListInitialized: Boolean = false,
        val products: Cached<List<Product>> = Cached.NotLoaded(),
        val deleteResponse: SupabaseResource<Boolean> = SupabaseResource.Idle
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        object GoAddNew : Inputs()
        data class GoEdit(val productId: Int) : Inputs()
        object GoCategory : Inputs()
        data class FetchProductList(val forceRefresh: Boolean) : Inputs()
        data class UpdateProductList(val products: Cached<List<Product>>) : Inputs()
        data class Delete(val product: Product) : Inputs()
        data class UpdateDeleteResponse(val response: SupabaseResource<Boolean>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
        object GoProductAdd : Events()
        data class GoProductEdit(val productId: Int) : Events()
        object GoCategories : Events()
    }
}
