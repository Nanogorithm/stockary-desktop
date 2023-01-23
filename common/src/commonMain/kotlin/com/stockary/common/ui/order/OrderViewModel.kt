package com.stockary.common.ui.order

import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class OrderViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: OrderEventHandler
) : BasicViewModel<OrderContract.Inputs, OrderContract.Events, OrderContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder.build(),
    eventHandler = eventHandler,
)
