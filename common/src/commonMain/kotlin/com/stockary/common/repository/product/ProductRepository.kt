package com.stockary.common.repository.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun clearAllCaches()
    fun getDataList(refreshCache: Boolean = false): Flow<Cached<List<Product>>>
    fun getCustomerTypes(refreshCache: Boolean = false): Flow<Cached<List<Role>>>
    fun getProductUnitTypes(refreshCache: Boolean = false): Flow<Cached<List<UnitType>>>

    fun add(product: Product, prices: List<String>, types: List<Role>): Flow<SupabaseResource<Boolean>>
    fun edit(product: Product, updated: Product): Flow<SupabaseResource<Boolean>>
    fun delete(product: Product): Flow<SupabaseResource<Boolean>>
}
