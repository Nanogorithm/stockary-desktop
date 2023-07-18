package com.stockary.common.ui.order

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.path
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class OrderEventHandler(
    val router: Router<AppScreen>
) : EventHandler<
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

        is OrderContract.Events.NavigateDetails -> {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    AppScreen.OrderDetails.directions()
                        .path(event.orderId)
                        .build()
                )
            )
            Unit
        }
    }
}
