package com.stockary.common.ui.summary

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.order.model.Order
import com.stockary.common.repository.order.model.OrderSummary

object SummaryContract {
    data class State(
        val loading: Boolean = false,
        val orders: Cached<List<OrderSummary>> = Cached.NotLoaded()
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        data class FetchOrders(val forceRefresh: Boolean) : Inputs()
        data class UpdateOrders(val orders: Cached<List<OrderSummary>>) : Inputs()

        data class Print(val orders: List<OrderSummary>): Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
