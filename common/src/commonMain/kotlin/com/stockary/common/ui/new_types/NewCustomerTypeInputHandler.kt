package com.stockary.common.ui.new_types

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.type.CustomerTypeRepository
import kotlinx.coroutines.flow.map

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

        NewCustomerTypeContract.Inputs.AddNew -> {
            val currentState = getCurrentState()
            val rawData = currentState.formState.getMap()
            println("formData => $rawData")
            updateState { it.copy(savingResponse = SupabaseResource.Loading) }
            observeFlows("AddNewCustomer") {
                listOf(customerTypeRepository.add(
                    title = rawData[Role::title.name] as String,
                    slug = rawData[Role::slug.name] as String
                ).map { NewCustomerTypeContract.Inputs.UpdateSavingResponse(it) })
            }
        }

        NewCustomerTypeContract.Inputs.Update -> {

        }

        is NewCustomerTypeContract.Inputs.UpdateSavingResponse -> {
            updateState { it.copy(savingResponse = input.savingResponse) }
        }
    }
}
