package com.stockary.common.ui.home

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.order.OrderRepository
import kotlinx.coroutines.flow.map

class HomeInputHandler(
    val orderRepository: OrderRepository
) : InputHandler<HomeContract.Inputs, HomeContract.Events, HomeContract.State> {
    override suspend fun InputHandlerScope<HomeContract.Inputs, HomeContract.Events, HomeContract.State>.handleInput(
        input: HomeContract.Inputs
    ) = when (input) {
        is HomeContract.Inputs.Initialize -> {
            postInput(HomeContract.Inputs.FetchOrders(true))
        }

        is HomeContract.Inputs.GoBack -> {
            postEvent(HomeContract.Events.NavigateUp)
        }

        is HomeContract.Inputs.FetchOrders -> {
            observeFlows("FetchOrders") {
                listOf(orderRepository.getDataList(input.forceRefresh).map { HomeContract.Inputs.UpdateOrders(it) })
            }
        }

        is HomeContract.Inputs.UpdateOrders -> {
            updateState { it.copy(orders = input.orders) }
        }
    }
}
