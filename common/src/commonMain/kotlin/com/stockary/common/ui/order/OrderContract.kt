package com.stockary.common.ui.order

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.order.model.Order
import com.stockary.common.today
import java.util.Date

object OrderContract {
    data class State(
        val loading: Boolean = false,
        val orders: Cached<List<Order>> = Cached.NotLoaded()
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        data class FetchOrders(
            val forceRefresh: Boolean,
            val date: Date = today(),
            val isSingleDay: Boolean = true
        ) : Inputs()

        data class UpdateOrders(val orders: Cached<List<Order>>) : Inputs()
        data class GoDetails(val orderId: String) : Inputs()

        data class PrintInvoices(val orders: List<Order>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
        data class NavigateDetails(val orderId: String) : Events()
    }
}
