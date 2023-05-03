package com.stockary.common.ui.new_types

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import kotlinx.coroutines.CoroutineScope

class NewCustomerTypeViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: NewCustomerTypeEventHandler
) : BasicViewModel<
        NewCustomerTypeContract.Inputs,
        NewCustomerTypeContract.Events,
        NewCustomerTypeContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder
        .build(),
    eventHandler = eventHandler,
)
