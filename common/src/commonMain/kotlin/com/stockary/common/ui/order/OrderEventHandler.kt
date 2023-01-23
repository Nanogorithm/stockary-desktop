package com.stockary.common.ui.order

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class OrderEventHandler(router: Router<AppScreen>) : EventHandler<
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
