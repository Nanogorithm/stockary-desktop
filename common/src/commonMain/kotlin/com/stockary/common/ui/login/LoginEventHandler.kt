package com.stockary.common.ui.login

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class LoginEventHandler(
    val router: Router<AppScreen>
) : EventHandler<
        LoginContract.Inputs,
        LoginContract.Events,
        LoginContract.State> {
    override suspend fun EventHandlerScope<
            LoginContract.Inputs,
            LoginContract.Events,
            LoginContract.State>.handleEvent(
        event: LoginContract.Events
    ) = when (event) {
        is LoginContract.Events.NavigateUp -> {

        }

        LoginContract.Events.LoginSuccess -> {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    AppScreen.Home.directions().build()
                )
            )
            Unit
        }
    }
}
