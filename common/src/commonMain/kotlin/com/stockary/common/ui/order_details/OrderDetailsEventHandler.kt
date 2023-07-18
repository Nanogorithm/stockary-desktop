package com.stockary.common.ui.order_details

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

public class OrderDetailsEventHandler(
    val router: Router<AppScreen>
) : EventHandler<
        OrderDetailsContract.Inputs,
        OrderDetailsContract.Events,
        OrderDetailsContract.State> {
    override suspend fun EventHandlerScope<
            OrderDetailsContract.Inputs,
            OrderDetailsContract.Events,
            OrderDetailsContract.State>.handleEvent(
        event: OrderDetailsContract.Events
    ) = when (event) {
        is OrderDetailsContract.Events.NavigateUp -> {

        }
    }
}
