package com.stockary.common.ui.new_product

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import kotlinx.coroutines.CoroutineScope

class NewProductViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: NewProductEventHandler
) : BasicViewModel<
        NewProductContract.Inputs,
        NewProductContract.Events,
        NewProductContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder
        .build(),
    eventHandler = eventHandler,
)
