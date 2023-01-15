package com.stockary.common.ui.product

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import kotlinx.coroutines.CoroutineScope

class ProductViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: ProductEventHandler
) : BasicViewModel<ProductContract.Inputs, ProductContract.Events, ProductContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder.build(),
    eventHandler = eventHandler,
)
