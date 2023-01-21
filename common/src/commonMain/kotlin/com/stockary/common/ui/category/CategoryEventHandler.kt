package com.stockary.common.ui.category

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class CategoryEventHandler(
    val router: Router<AppScreen>
) : EventHandler<CategoryContract.Inputs, CategoryContract.Events, CategoryContract.State> {
    override suspend fun EventHandlerScope<CategoryContract.Inputs, CategoryContract.Events, CategoryContract.State>.handleEvent(
        event: CategoryContract.Events
    ) = when (event) {
        is CategoryContract.Events.NavigateUp -> {

        }

        CategoryContract.Events.AddNew -> {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    AppScreen.NewCategory.directions().build()
                )
            )
            Unit
        }
    }
}
