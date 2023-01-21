package com.stockary.common.repository.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType

object ProductRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val dataList: Cached<List<Product>> = Cached.NotLoaded(),

        val customerTypesInitialized: Boolean = false,
        val customerTypes: Cached<List<Role>> = Cached.NotLoaded(),

        val unitTypes: Cached<List<UnitType>> = Cached.NotLoaded(),

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

        data class RefreshUnitTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateUnitTypes(val unitTypes: Cached<List<UnitType>>) : Inputs()

        data class RefreshCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val dataList: Cached<List<Role>>) : Inputs()

        data class Add(val product: Product, val prices: List<String>, val types: List<Role>) : Inputs()
        data class Edit(val product: Product, val updated: Product) : Inputs()
        data class Delete(val product: Product) : Inputs()
    }
}
