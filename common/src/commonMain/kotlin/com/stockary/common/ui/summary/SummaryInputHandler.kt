package com.stockary.common.ui.summary

import androidx.compose.runtime.SideEffect
import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.order.OrderRepository
import kotlinx.coroutines.flow.map

class SummaryInputHandler(
    val orderRepository: OrderRepository
) : InputHandler<SummaryContract.Inputs, SummaryContract.Events, SummaryContract.State> {
    override suspend fun InputHandlerScope<SummaryContract.Inputs, SummaryContract.Events, SummaryContract.State>.handleInput(
        input: SummaryContract.Inputs
    ) = when (input) {
        is SummaryContract.Inputs.Initialize -> {
            postInput(SummaryContract.Inputs.FetchOrders(true))
        }

        is SummaryContract.Inputs.GoBack -> {
            postEvent(SummaryContract.Events.NavigateUp)
        }

        is SummaryContract.Inputs.FetchOrders -> {
            observeFlows("FetchOrders") {
                listOf(orderRepository.getOrders(input.forceRefresh).map { SummaryContract.Inputs.UpdateOrders(it) })
            }
        }

        is SummaryContract.Inputs.UpdateOrders -> {
            updateState { it.copy(orders = input.orders) }
        }

        is SummaryContract.Inputs.Print -> {
            pdfInvoice(fileName = System.currentTimeMillis().toString(), orders = input.orders)
        }
    }
}
