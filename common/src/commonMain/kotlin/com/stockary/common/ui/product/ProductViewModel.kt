package com.stockary.common.ui.product

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class ProductViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    inputHandler: ProductInputHandler
) : BasicViewModel<
        ProductContract.Inputs,
        ProductContract.Events,
        ProductContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder
        .withViewModel(
            inputHandler = inputHandler,
            initialState = ProductContract.State(),
            name = "Product",
        )
        .build(),
    eventHandler = ProductEventHandler(),
)
