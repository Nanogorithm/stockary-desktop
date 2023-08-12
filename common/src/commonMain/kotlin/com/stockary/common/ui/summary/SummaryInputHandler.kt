package com.stockary.common.ui.summary

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.order.OrderRepository
import com.stockary.common.repository.order.model.OrderSummaryTable
import com.stockary.common.repository.order.model.toOrderSummaryItem
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
                listOf(
                    orderRepository.getTodayOrders(input.forceRefresh).map { SummaryContract.Inputs.UpdateOrders(it) })
            }
        }

        is SummaryContract.Inputs.UpdateOrders -> {
            updateState { it.copy(orders = input.orders) }
        }

        is SummaryContract.Inputs.Print -> {
            sideJob("printSummary") {
                pdfInvoiceForSummary(
                    fileName = System.currentTimeMillis().toString(),
                    orders = input.orders.flatMap { _order ->
                        _order.toOrderSummaryItem()
                    }.groupBy {
                        it.productId
                    }.map {
                        println("${it.key} => ${it.value.map { it.productName }}")
                        val totalUnitAmount = it.value.map { item -> (item.units?.amount ?: 0f) * item.quantity }.sum()
                        val first = it.value.firstOrNull()
                        OrderSummaryTable(
                            userId = null,
                            customerName = null,
                            productName = first?.productName,
                            categoryName = first?.category,
                            totalUnit = totalUnitAmount,
                            unitName = first?.units?.type ?: ""
                        )
                    }.sortedBy {
                        it.categoryName
                    }
                )
            }
        }
    }
}
