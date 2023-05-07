package com.stockary.common.repository.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType
import kotlinx.coroutines.flow.Flow
import java.io.File

interface ProductRepository {
    fun clearAllCaches()
    fun getProductList(refreshCache: Boolean = false): Flow<Cached<List<Product>>>
    fun getCustomerTypes(refreshCache: Boolean = false): Flow<Cached<List<Role>>>
    fun getProductUnitTypes(refreshCache: Boolean = false): Flow<Cached<List<UnitType>>>

    fun uploadPhoto(file: File): Flow<SupabaseResource<String>>

    fun get(productId: String): Flow<SupabaseResource<Product>>
    fun add(product: Product, prices: List<Float>, types: List<Role>): Flow<SupabaseResource<Boolean>>
    fun edit(product: Product, updated: Product, prices: List<Float>, types: List<Role>): Flow<SupabaseResource<Boolean>>
    fun delete(product: Product): Flow<SupabaseResource<Boolean>>
    fun getPhotoUrl(url: String)
}
