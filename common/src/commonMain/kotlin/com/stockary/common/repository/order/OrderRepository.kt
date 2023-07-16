package com.stockary.common.repository.order

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.order.model.Order
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun clearAllCaches()
    fun getOrders(refreshCache: Boolean = false): Flow<Cached<List<Order>>>
    fun getTodayOrders(refreshCache: Boolean): Flow<Cached<List<Order>>>
}
