package com.stockary.common.repository.order

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.order.model.Order
import com.stockary.common.today
import java.util.*

object OrderRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val orderList: Cached<List<Order>> = Cached.NotLoaded(),
        val summary: Cached<List<Order>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshOrders(
            val forceRefresh: Boolean,
            val date: Date = today(),
            val isSingleDay: Boolean = true
        ) : Inputs()

        data class UpdateOrders(val dataList: Cached<List<Order>>) : Inputs()

        data class RefreshSummary(val forceRefresh: Boolean) : Inputs()
        data class UpdateSummary(val summary: Cached<List<Order>>) : Inputs()
    }
}
