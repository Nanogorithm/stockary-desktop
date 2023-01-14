package com.stockary.common.ui.customer

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

class CustomerEventHandler : EventHandler<
        CustomerContract.Inputs,
        CustomerContract.Events,
        CustomerContract.State> {
    override suspend fun EventHandlerScope<
            CustomerContract.Inputs,
            CustomerContract.Events,
            CustomerContract.State>.handleEvent(
        event: CustomerContract.Events
    ) = when (event) {
        is CustomerContract.Events.NavigateUp -> {

        }
    }
}
