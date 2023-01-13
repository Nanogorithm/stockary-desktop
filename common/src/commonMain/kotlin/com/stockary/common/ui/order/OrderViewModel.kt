package com.stockary.common.ui.order

import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class OrderViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    inputHandler: OrderInputHandler
) : BasicViewModel<OrderContract.Inputs, OrderContract.Events, OrderContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder.withViewModel(
        inputHandler = inputHandler,
        initialState = OrderContract.State(),
        name = "Order",
    ).build(),
    eventHandler = OrderEventHandler(),
)
