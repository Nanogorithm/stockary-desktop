package com.stockary.common.ui.app

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class AppEventHandler(
    val router: Router<AppScreen>
) : EventHandler<
        AppContract.Inputs,
        AppContract.Events,
        AppContract.State> {
    override suspend fun EventHandlerScope<
            AppContract.Inputs,
            AppContract.Events,
            AppContract.State>.handleEvent(
        event: AppContract.Events
    ) = when (event) {
        is AppContract.Events.NavigateUp -> {

        }

        AppContract.Events.LoginScreen -> {
            router.trySend(RouterContract.Inputs.ReplaceTopDestination(AppScreen.Login.directions().build()))
            Unit
        }
    }
}
