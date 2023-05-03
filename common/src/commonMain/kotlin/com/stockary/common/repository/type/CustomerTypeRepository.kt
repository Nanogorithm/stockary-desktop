package com.stockary.common.repository.type

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import kotlinx.coroutines.flow.Flow

interface CustomerTypeRepository {

    fun clearAllCaches()
    fun getCustomerTypeList(refreshCache: Boolean = false): Flow<Cached<List<Role>>>
    fun add(title: String, slug: String): Flow<SupabaseResource<Boolean>>

    fun get(typeId: String): Flow<SupabaseResource<Role>>
    fun edit(
        type: Role, updated: Role
    ): Flow<SupabaseResource<Boolean>>

    fun delete(role: Role): Flow<SupabaseResource<Boolean>>
}
