package com.stockary.common.repository.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.CategoryRepositoryContract
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.product.model.Product

object ProductRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val dataList: Cached<List<Product>> = Cached.NotLoaded(),
        val saving: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val updating: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val deleting: SupabaseResource<Boolean> = SupabaseResource.Idle
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshDataList(val forceRefresh: Boolean) : Inputs()
        data class DataListUpdated(val dataList: Cached<List<Product>>) : Inputs()

        data class Add(val product: Product) : Inputs()
        data class Edit(val product: Product, val updated: Product) : Inputs()
        data class Delete(val product: Product) : Inputs()
    }
}
