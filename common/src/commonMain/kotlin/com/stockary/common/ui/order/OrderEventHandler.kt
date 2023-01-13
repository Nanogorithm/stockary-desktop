package com.stockary.common.ui.order

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

class OrderEventHandler : EventHandler<
        OrderContract.Inputs,
        OrderContract.Events,
        OrderContract.State> {
    override suspend fun EventHandlerScope<
            OrderContract.Inputs,
            OrderContract.Events,
            OrderContract.State>.handleEvent(
        event: OrderContract.Events
    ) = when (event) {
        is OrderContract.Events.NavigateUp -> {

        }
    }
}
