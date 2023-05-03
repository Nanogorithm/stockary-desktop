package com.stockary.common.ui.new_types

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.stockary.common.repository.type.CustomerTypeRepository

class NewCustomerTypeInputHandler(
    private val customerTypeRepository: CustomerTypeRepository
) : InputHandler<
        NewCustomerTypeContract.Inputs,
        NewCustomerTypeContract.Events,
        NewCustomerTypeContract.State> {
    override suspend fun InputHandlerScope<
            NewCustomerTypeContract.Inputs,
            NewCustomerTypeContract.Events,
            NewCustomerTypeContract.State>.handleInput(
        input: NewCustomerTypeContract.Inputs
    ) = when (input) {
        is NewCustomerTypeContract.Inputs.Initialize -> {
            updateState { it.copy(typeId = input.typeId) }
        }

        is NewCustomerTypeContract.Inputs.GoBack -> {
            postEvent(NewCustomerTypeContract.Events.NavigateUp)
        }
    }
}
