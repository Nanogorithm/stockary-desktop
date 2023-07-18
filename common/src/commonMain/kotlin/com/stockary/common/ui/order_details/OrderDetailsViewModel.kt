package com.stockary.common.ui.order_details

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import kotlinx.coroutines.CoroutineScope

public class OrderDetailsViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: OrderDetailsEventHandler
) : BasicViewModel<
        OrderDetailsContract.Inputs,
        OrderDetailsContract.Events,
        OrderDetailsContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder
        .build(),
    eventHandler = eventHandler,
)
