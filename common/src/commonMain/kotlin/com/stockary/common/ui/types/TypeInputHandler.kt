package com.stockary.common.ui.types

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.type.CustomerTypeRepository
import kotlinx.coroutines.flow.map

class TypeInputHandler(
    private val customerTypeRepository: CustomerTypeRepository
) : InputHandler<TypeContract.Inputs, TypeContract.Events, TypeContract.State> {
    override suspend fun InputHandlerScope<TypeContract.Inputs, TypeContract.Events, TypeContract.State>.handleInput(
        input: TypeContract.Inputs
    ) = when (input) {
        is TypeContract.Inputs.Initialize -> {
            postInput(TypeContract.Inputs.RefreshCustomerTypeList(true))
        }

        is TypeContract.Inputs.GoBack -> {
            postEvent(TypeContract.Events.NavigateUp)
        }

        is TypeContract.Inputs.RefreshCustomerTypeList -> {
            observeFlows("RefreshCustomerTypeList") {
                listOf(customerTypeRepository.getCustomerTypeList(input.forceRefresh)
                    .map { TypeContract.Inputs.UpdateCustomerTypeList(it) })
            }
        }

        is TypeContract.Inputs.UpdateCustomerTypeList -> {
            updateState { it.copy(types = input.types) }
        }

        is TypeContract.Inputs.GoNewCustomerType -> {
            postEvent(TypeContract.Events.NavigateNewCustomerType(input.typeId))
        }

        is TypeContract.Inputs.Delete -> {
            sideJob("DeleteType") {
                customerTypeRepository.delete(input.type)
            }
        }

        is TypeContract.Inputs.Edit -> {

        }
    }
}
