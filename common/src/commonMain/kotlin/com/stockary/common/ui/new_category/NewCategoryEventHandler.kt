package com.stockary.common.ui.new_category

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class NewCategoryEventHandler(
    val router: Router<AppScreen>
) : EventHandler<NewCategoryContract.Inputs, NewCategoryContract.Events, NewCategoryContract.State> {
    override suspend fun EventHandlerScope<NewCategoryContract.Inputs, NewCategoryContract.Events, NewCategoryContract.State>.handleEvent(
        event: NewCategoryContract.Events
    ) = when (event) {
        is NewCategoryContract.Events.NavigateUp -> {
            router.trySend(
                RouterContract.Inputs.GoBack()
            )
            Unit
        }
    }
}
