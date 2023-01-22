package com.stockary.common.ui.customer

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class CustomerEventHandler(
    val router: Router<AppScreen>
) : EventHandler<
        CustomerContract.Inputs,
        CustomerContract.Events,
        CustomerContract.State> {
    override suspend fun EventHandlerScope<
            CustomerContract.Inputs,
            CustomerContract.Events,
            CustomerContract.State>.handleEvent(
        event: CustomerContract.Events
    ) = when (event) {
        is CustomerContract.Events.NavigateUp -> {
            
        }
    }
}
