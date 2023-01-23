package com.stockary.common.ui.summary

import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class SummaryViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: SummaryEventHandler
) : BasicViewModel<SummaryContract.Inputs, SummaryContract.Events, SummaryContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder.build(),
    eventHandler = eventHandler,
)
