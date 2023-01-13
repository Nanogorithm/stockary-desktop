package com.stockary.common.ui.order

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.order.OrderRepository
import kotlinx.coroutines.flow.map

class OrderInputHandler(
    val orderRepository: OrderRepository
) : InputHandler<OrderContract.Inputs, OrderContract.Events, OrderContract.State> {
    override suspend fun InputHandlerScope<OrderContract.Inputs, OrderContract.Events, OrderContract.State>.handleInput(
        input: OrderContract.Inputs
    ) = when (input) {
        is OrderContract.Inputs.Initialize -> {
            postInput(OrderContract.Inputs.FetchOrders(forceRefresh = true))
        }

        is OrderContract.Inputs.GoBack -> {
            postEvent(OrderContract.Events.NavigateUp)
        }

        is OrderContract.Inputs.FetchOrders -> {
            observeFlows("FetchOrders") {
                listOf(orderRepository.getDataList(refreshCache = input.forceRefresh).map {
                    OrderContract.Inputs.UpdateOrders(it)
                })
            }
        }

        is OrderContract.Inputs.UpdateOrders -> {
            updateState { it.copy(orders = input.orders) }
        }
    }
}
