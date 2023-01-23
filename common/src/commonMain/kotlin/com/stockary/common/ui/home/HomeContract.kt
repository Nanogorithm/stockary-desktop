package com.stockary.common.ui.home

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.order.model.Order

object HomeContract {
    data class State(
        val loading: Boolean = false, val orders: Cached<List<Order>> = Cached.NotLoaded()
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        data class FetchOrders(val forceRefresh: Boolean) : Inputs()
        data class UpdateOrders(val orders: Cached<List<Order>>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
