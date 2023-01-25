package com.stockary.common.ui.product

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.build
import com.copperleaf.ballast.navigation.routing.directions
import com.copperleaf.ballast.navigation.routing.queryParameter
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class ProductEventHandler(
    val router: Router<AppScreen>
) : EventHandler<ProductContract.Inputs, ProductContract.Events, ProductContract.State> {
    override suspend fun EventHandlerScope<ProductContract.Inputs, ProductContract.Events, ProductContract.State>.handleEvent(
        event: ProductContract.Events
    ) = when (event) {
        is ProductContract.Events.NavigateUp -> {
            router.trySend(
                RouterContract.Inputs.GoBack()
            )
            Unit
        }

        ProductContract.Events.GoCategories -> {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    AppScreen.CategoryList.directions().build()
                )
            )
            Unit
        }

        ProductContract.Events.GoProductAdd -> {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    AppScreen.NewProduct.directions().build()
                )
            )
            Unit
        }

        is ProductContract.Events.GoProductEdit -> {
            router.trySend(
                RouterContract.Inputs.GoToDestination(
                    AppScreen.NewProduct.directions()
                        .queryParameter("productId", event.productId.toString())
                        .build()
                )
            )
            Unit
        }
    }
}
