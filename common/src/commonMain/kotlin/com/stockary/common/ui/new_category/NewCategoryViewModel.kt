package com.stockary.common.ui.new_category

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import kotlinx.coroutines.CoroutineScope

class NewCategoryViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: NewCategoryEventHandler
) : BasicViewModel<
        NewCategoryContract.Inputs,
        NewCategoryContract.Events,
        NewCategoryContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder
        .build(),
    eventHandler = eventHandler,
)
