package com.stockary.common.ui.types

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class TypeEventHandler(
    private val router: Router<AppScreen>
) : EventHandler<TypeContract.Inputs, TypeContract.Events, TypeContract.State> {
    override suspend fun EventHandlerScope<TypeContract.Inputs, TypeContract.Events, TypeContract.State>.handleEvent(
        event: TypeContract.Events
    ) = when (event) {
        is TypeContract.Events.NavigateUp -> {
            router.trySend(
                RouterContract.Inputs.GoBack()
            )
            Unit
        }

        is TypeContract.Events.NavigateNewCustomerType -> {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    AppScreen.NewCustomerType.directions().build()
                )
            )
            Unit
        }
    }
}
