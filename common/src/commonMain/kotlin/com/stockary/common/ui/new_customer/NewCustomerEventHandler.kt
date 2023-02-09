package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

class NewCustomerEventHandler : EventHandler<
        NewCustomerContract.Inputs,
        NewCustomerContract.Events,
        NewCustomerContract.State> {
    override suspend fun EventHandlerScope<
            NewCustomerContract.Inputs,
            NewCustomerContract.Events,
            NewCustomerContract.State>.handleEvent(
        event: NewCustomerContract.Events
    ) = when (event) {
        is NewCustomerContract.Events.NavigateUp -> {

        }
    }
}
