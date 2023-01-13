package com.stockary.common.ui.product

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

class ProductEventHandler : EventHandler<
        ProductContract.Inputs,
        ProductContract.Events,
        ProductContract.State> {
    override suspend fun EventHandlerScope<
            ProductContract.Inputs,
            ProductContract.Events,
            ProductContract.State>.handleEvent(
        event: ProductContract.Events
    ) = when (event) {
        is ProductContract.Events.NavigateUp -> {
            
        }
    }
}
