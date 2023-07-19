package com.stockary.common.repository.order

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.order.model.Order
import com.stockary.common.today
import kotlinx.coroutines.flow.Flow
import java.util.*

interface OrderRepository {
    fun clearAllCaches()
    fun getOrders(refreshCache: Boolean = false, date: Date, isSingleDay: Boolean): Flow<Cached<List<Order>>>
    fun getTodayOrders(refreshCache: Boolean): Flow<Cached<List<Order>>>
}
