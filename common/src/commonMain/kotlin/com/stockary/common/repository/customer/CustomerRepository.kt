package com.stockary.common.repository.customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.product.model.Product
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun clearAllCaches()
    fun getCustomerList(refreshCache: Boolean = false): Flow<Cached<List<Profile>>>

    fun get(customerId: Int): Flow<SupabaseResource<Product>>
    fun add(email: String, password: String, roleId: Int): Flow<SupabaseResource<Email.Result>>
    fun edit(
        customer: Profile, updated: Profile
    ): Flow<SupabaseResource<Boolean>>

    fun delete(customer: Profile): Flow<SupabaseResource<Boolean>>
}
