package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class NewCustomerEventHandler(
    private val router: Router<AppScreen>
) : EventHandler<NewCustomerContract.Inputs, NewCustomerContract.Events, NewCustomerContract.State> {
    override suspend fun EventHandlerScope<NewCustomerContract.Inputs, NewCustomerContract.Events, NewCustomerContract.State>.handleEvent(
        event: NewCustomerContract.Events
    ) = when (event) {
        is NewCustomerContract.Events.NavigateUp -> {
            router.trySend(RouterContract.Inputs.GoBack())
            Unit
        }
    }
}
