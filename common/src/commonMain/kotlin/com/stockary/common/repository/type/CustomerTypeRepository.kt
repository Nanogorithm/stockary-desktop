package com.stockary.common.repository.type

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.customer.model.Role
import kotlinx.coroutines.flow.Flow

interface CustomerTypeRepository {

    fun clearAllCaches()
    fun getCustomerTypeList(refreshCache: Boolean = false): Flow<Cached<List<Role>>>

}
