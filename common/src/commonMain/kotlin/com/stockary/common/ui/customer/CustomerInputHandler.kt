package com.stockary.common.ui.customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.customer.CustomerRepository
import kotlinx.coroutines.flow.map

class CustomerInputHandler(
    val customerRepository: CustomerRepository
) : InputHandler<CustomerContract.Inputs, CustomerContract.Events, CustomerContract.State> {
    override suspend fun InputHandlerScope<CustomerContract.Inputs, CustomerContract.Events, CustomerContract.State>.handleInput(
        input: CustomerContract.Inputs
    ) = when (input) {
        is CustomerContract.Inputs.Initialize -> {
            postInput(CustomerContract.Inputs.FetchCustomerList(forceRefresh = true))
        }

        is CustomerContract.Inputs.GoBack -> {
            postEvent(CustomerContract.Events.NavigateUp)
        }

        is CustomerContract.Inputs.FetchCustomerList -> {
            observeFlows("FetchCustomerList") {
                listOf(customerRepository.getDataList(input.forceRefresh)
                    .map { CustomerContract.Inputs.UpdateCustomerList(it) })
            }
        }

        is CustomerContract.Inputs.UpdateCustomerList -> {
            updateState { it.copy(customers = input.customers) }
        }
    }
}
