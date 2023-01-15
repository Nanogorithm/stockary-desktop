package com.stockary.common.ui.new_product

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class NewProductEventHandler(
    val router: Router<AppScreen>
) : EventHandler<NewProductContract.Inputs, NewProductContract.Events, NewProductContract.State> {
    override suspend fun EventHandlerScope<NewProductContract.Inputs, NewProductContract.Events, NewProductContract.State>.handleEvent(
        event: NewProductContract.Events
    ) = when (event) {
        is NewProductContract.Events.NavigateUp -> {
            router.trySend(RouterContract.Inputs.GoBack())
            Unit
        }
    }
}
