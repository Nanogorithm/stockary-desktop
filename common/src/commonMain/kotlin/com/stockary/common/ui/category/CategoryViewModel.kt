package com.stockary.common.ui.category

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import kotlinx.coroutines.CoroutineScope

class CategoryViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BasicViewModel<CategoryContract.Inputs, CategoryContract.Events, CategoryContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder.build(),
    eventHandler = CategoryEventHandler(),
)
