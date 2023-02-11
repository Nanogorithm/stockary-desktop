package com.stockary.common.repository.customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Product
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun clearAllCaches()
    fun getCustomerList(refreshCache: Boolean = false): Flow<Cached<List<Profile>>>

    fun getCustomerTypes(refreshCache: Boolean = false): Flow<Cached<List<Role>>>

    fun get(customerId: Int): Flow<SupabaseResource<Product>>
    fun add(email: String, name: String, roleId: Int, address: String): Flow<SupabaseResource<Boolean>>
    fun edit(
        customer: Profile, updated: Profile
    ): Flow<SupabaseResource<Boolean>>

    fun delete(customer: Profile): Flow<SupabaseResource<Boolean>>
}
