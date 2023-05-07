package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.ChoiceState
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.repository.customer.CustomerRepository
import com.stockary.common.repository.customer.model.Profile
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
            input.customerId?.let {
                postInput(NewCustomerContract.Inputs.GetCustomer(it))
            }
            Unit
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
                    email = rawData[Profile::email.name] as String,
                    name = rawData[Profile::name.name] as String,
                    role = (rawData[Profile::role.name] as String),
                    address = rawData[Profile::address.name] as String,
                    phone = rawData[Profile::phone.name] as String
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
            updateState { it.copy(savingResponse = SupabaseResource.Loading) }
            val currentState = getCurrentState()
            val rawData = currentState.formState.getMap()

            if (currentState.customer is SupabaseResource.Success) {
                val customer = currentState.customer.data
                val updated = customer.copy().apply {
                    email = rawData[Profile::email.name] as String
                    name = rawData[Profile::name.name] as String
                    role = (rawData[Profile::role.name] as String)
                    address = rawData[Profile::address.name] as String
                    phone = rawData[Profile::phone.name] as String
                }

                println("updated => $updated")

                observeFlows("UpdateCustomer") {
                    listOf(
                        customerRepository.edit(
                            customer = customer, updated = updated
                        ).map { NewCustomerContract.Inputs.UpdateSavingResponse(it) }
                    )
                }
            } else {
                updateState { it.copy(savingResponse = SupabaseResource.Error(Exception("Update not possible right now. try later."))) }
            }
            Unit
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

        is NewCustomerContract.Inputs.GetCustomer -> {
            updateState { it.copy(customer = SupabaseResource.Loading) }
            observeFlows("GetProduct") {
                listOf(customerRepository.get(input.customerId).map { NewCustomerContract.Inputs.UpdateCustomer(it) })
            }
        }

        is NewCustomerContract.Inputs.UpdateCustomer -> {
            updateState { it.copy(customer = input.customer) }
            postInput(NewCustomerContract.Inputs.UpdateFormData)
        }

        NewCustomerContract.Inputs.UpdateFormData -> {
            val currentState = getCurrentState()

            if (currentState.customer is SupabaseResource.Success) {
                val formState = currentState.formState
                val customer: Profile = currentState.customer.data

                formState.getState<TextFieldState>(Profile::name.name).change(customer.name)
                formState.getState<TextFieldState>(Profile::email.name).change(customer.email ?: "")
                formState.getState<TextFieldState>(Profile::phone.name).change(customer.phone ?: "")
                formState.getState<TextFieldState>(Profile::address.name).change(customer.address ?: "")
                formState.getState<ChoiceState>(Profile::role.name).change(customer.role ?: "")
            }

            Unit
        }
    }
}
