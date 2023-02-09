package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class NewCustomerViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BasicViewModel<
        NewCustomerContract.Inputs,
        NewCustomerContract.Events,
        NewCustomerContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder
        .withViewModel(
            inputHandler = NewCustomerInputHandler(),
            initialState = NewCustomerContract.State(),
            name = "NewCustomer",
        )
        .build(),
    eventHandler = NewCustomerEventHandler(),
)
