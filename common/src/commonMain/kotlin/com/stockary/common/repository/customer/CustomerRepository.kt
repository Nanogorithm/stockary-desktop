package com.stockary.common.repository.customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.customer.model.Profile
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    fun clearAllCaches()
    fun getCustomerList(refreshCache: Boolean = false): Flow<Cached<List<Profile>>>
}
