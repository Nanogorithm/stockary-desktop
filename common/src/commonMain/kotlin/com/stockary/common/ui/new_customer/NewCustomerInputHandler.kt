package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.stockary.common.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.map

class NewCustomerInputHandler(
    private val customerRepository: CustomerRepository
) : InputHandler<NewCustomerContract.Inputs, NewCustomerContract.Events, NewCustomerContract.State> {
    override suspend fun InputHandlerScope<NewCustomerContract.Inputs, NewCustomerContract.Events, NewCustomerContract.State>.handleInput(
        input: NewCustomerContract.Inputs
    ) = when (input) {
        is NewCustomerContract.Inputs.Initialize -> {
            updateState { it.copy(customerId = input.customerId) }
        }

        is NewCustomerContract.Inputs.GoBack -> {
            postEvent(NewCustomerContract.Events.NavigateUp)
        }

        is NewCustomerContract.Inputs.AddNew -> {
            val currentState = getCurrentState()
            val rawData = currentState.formState.getMap()
            observeFlows("AddNewCustomer") {
                listOf(customerRepository.add(
                    email = rawData["email"] as String, password = rawData["password"] as String, roleId = 1
                ).map { NewCustomerContract.Inputs.UpdateSavingResponse(it) })
            }
        }

        is NewCustomerContract.Inputs.UpdateSavingResponse -> {
            updateState { it.copy(savingResponse = input.savingResponse) }
        }

        NewCustomerContract.Inputs.Update -> {

        }
    }
}
