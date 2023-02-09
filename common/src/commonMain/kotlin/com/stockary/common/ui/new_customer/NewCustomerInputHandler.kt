package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import kotlinx.coroutines.delay

class NewCustomerInputHandler : InputHandler<
        NewCustomerContract.Inputs,
        NewCustomerContract.Events,
        NewCustomerContract.State> {
    override suspend fun InputHandlerScope<
            NewCustomerContract.Inputs,
            NewCustomerContract.Events,
            NewCustomerContract.State>.handleInput(
        input: NewCustomerContract.Inputs
    ) = when (input) {
        is NewCustomerContract.Inputs.Initialize -> {
            updateState { it.copy(loading = true) }
            delay(1000)
            updateState { it.copy(loading = false) }
        }

        is NewCustomerContract.Inputs.GoBack -> {
            postEvent(NewCustomerContract.Events.NavigateUp)
        }
    }
}
