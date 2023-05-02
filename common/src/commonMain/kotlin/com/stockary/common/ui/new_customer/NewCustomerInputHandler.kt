package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.SupabaseResource
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
            postInput(NewCustomerContract.Inputs.FetchCustomerTypes(true))
        }

        is NewCustomerContract.Inputs.GoBack -> {
            postEvent(NewCustomerContract.Events.NavigateUp)
        }

        is NewCustomerContract.Inputs.AddNew -> {
            val currentState = getCurrentState()
            val rawData = currentState.formState.getMap()
            println("formData => $rawData")
            updateState { it.copy(savingResponse = SupabaseResource.Loading) }
            observeFlows("AddNewCustomer") {
                listOf(customerRepository.add(
                    email = rawData["email"] as String,
                    name = rawData["name"] as String,
                    role = (rawData["role"] as String),
                    address = rawData["address"] as String,
                    phone = rawData["phone"] as String
                ).map { NewCustomerContract.Inputs.UpdateSavingResponse(it) })
            }
        }

        is NewCustomerContract.Inputs.UpdateSavingResponse -> {
            updateState { it.copy(savingResponse = input.savingResponse) }
            val currentState = getCurrentState()
            when (input.savingResponse) {
                is SupabaseResource.Success -> {
                    
                }

                else -> {

                }
            }
        }

        NewCustomerContract.Inputs.Update -> {

        }

        is NewCustomerContract.Inputs.FetchCustomerTypes -> {
            observeFlows("FetchCustomerTypes") {
                listOf(customerRepository.getCustomerTypes(input.forceRefresh)
                    .map { NewCustomerContract.Inputs.UpdateCustomerTypes(it) })
            }
        }

        is NewCustomerContract.Inputs.UpdateCustomerTypes -> {
            updateState { it.copy(customerType = input.customerTypes) }
        }
    }
}
