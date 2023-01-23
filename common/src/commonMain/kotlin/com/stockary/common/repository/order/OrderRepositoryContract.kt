package com.stockary.common.repository.order

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.order.model.Order

object OrderRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val orderList: Cached<List<Order>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshDataList(val forceRefresh: Boolean) : Inputs()
        data class DataListUpdated(val dataList: Cached<List<Order>>) : Inputs()
    }
}
