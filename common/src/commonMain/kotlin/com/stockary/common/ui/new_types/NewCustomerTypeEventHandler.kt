package com.stockary.common.ui.new_types

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class NewCustomerTypeEventHandler(
    private val router: Router<AppScreen>
) : EventHandler<NewCustomerTypeContract.Inputs, NewCustomerTypeContract.Events, NewCustomerTypeContract.State> {
    override suspend fun EventHandlerScope<NewCustomerTypeContract.Inputs, NewCustomerTypeContract.Events, NewCustomerTypeContract.State>.handleEvent(
        event: NewCustomerTypeContract.Events
    ) = when (event) {
        is NewCustomerTypeContract.Events.NavigateUp -> {
            router.trySend(
                RouterContract.Inputs.GoBack()
            )
            Unit
        }
    }
}
