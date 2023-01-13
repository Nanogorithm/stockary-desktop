package com.stockary.common.ui.login

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.core.BasicViewModel
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.withViewModel
import kotlinx.coroutines.CoroutineScope

class LoginViewModel(
    coroutineScope: CoroutineScope,
    configBuilder: BallastViewModelConfiguration.Builder,
    eventHandler: LoginEventHandler
) : BasicViewModel<LoginContract.Inputs, LoginContract.Events, LoginContract.State>(
    coroutineScope = coroutineScope,
    config = configBuilder.apply {
        logger = { PrintlnLogger() }
        this += LoggingInterceptor()
    }.withViewModel(
        inputHandler = LoginInputHandler(),
        initialState = LoginContract.State(),
        name = "Login",
    ).build(),
    eventHandler = eventHandler,
)
