package com.stockary.common.repository.order

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.withRepository
import com.stockary.common.repository.order.model.Order
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.*

class OrderRepositoryImpl(
    coroutineScope: CoroutineScope,
    eventBus: EventBus,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BallastRepository<OrderRepositoryContract.Inputs, OrderRepositoryContract.State>(
    coroutineScope = coroutineScope, eventBus = eventBus, config = configBuilder.apply {
        inputHandler = OrderRepositoryInputHandler(eventBus)
        initialState = OrderRepositoryContract.State()
        name = "Order Repository"
    }.withRepository().build()
), OrderRepository {
    override fun clearAllCaches() {
        trySend(OrderRepositoryContract.Inputs.ClearCaches)
    }

    override fun getOrders(refreshCache: Boolean, date: Date, isSingleDay: Boolean): Flow<Cached<List<Order>>> {
        trySend(OrderRepositoryContract.Inputs.Initialize)
        trySend(OrderRepositoryContract.Inputs.RefreshOrders(refreshCache, date, isSingleDay))
        return observeStates().map { it.orderList }
    }

    override fun getTodayOrders(refreshCache: Boolean): Flow<Cached<List<Order>>> {
        trySend(OrderRepositoryContract.Inputs.RefreshSummary(refreshCache))
        return observeStates().map { it.summary }
    }
}
