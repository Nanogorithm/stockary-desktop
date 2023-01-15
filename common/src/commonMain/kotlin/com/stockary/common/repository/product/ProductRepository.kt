package com.stockary.common.repository.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.product.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun clearAllCaches()
    fun getDataList(refreshCache: Boolean = false): Flow<Cached<List<Product>>>

    fun add(product: Product): SupabaseResource<Boolean>
    fun edit(product: Product, updated: Product): SupabaseResource<Boolean>
    fun delete(product: Product): SupabaseResource<Boolean>
}
