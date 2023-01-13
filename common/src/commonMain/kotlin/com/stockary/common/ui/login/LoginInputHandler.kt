package com.stockary.common.ui.login

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.stockary.common.repository.login.LoginRepository
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LoginInputHandler : InputHandler<LoginContract.Inputs, LoginContract.Events, LoginContract.State>, KoinComponent {

    val loginRepository: LoginRepository by inject()

    override suspend fun InputHandlerScope<LoginContract.Inputs, LoginContract.Events, LoginContract.State>.handleInput(
        input: LoginContract.Inputs
    ) = when (input) {
        is LoginContract.Inputs.Initialize -> {
            updateState { it.copy(loading = false) }
            observeFlows(
                key = "ObserveLoginSession"
            ) {
                listOf(loginRepository.sessionStatus() // returns a Flow
                    .map {
                        LoginContract.Inputs.UpdateAuthStatus(it)
                    })
            }
        }

        is LoginContract.Inputs.GoBack -> {
            postEvent(LoginContract.Events.NavigateUp)
        }

        is LoginContract.Inputs.LoginByEmail -> {
            loginRepository.loginUser(email = input.email, password = input.password)
        }

        is LoginContract.Inputs.UpdateAuthStatus -> {
            when (input.sessionStatus) {
                is SessionStatus.Authenticated -> {
                    updateState { it.copy(isLoggedIn = true) }
                    postEvent(LoginContract.Events.LoginSuccess)
                }

                SessionStatus.LoadingFromStorage -> {
                    
                }

                SessionStatus.NetworkError -> {

                }

                SessionStatus.NotAuthenticated -> {

                }
            }
        }
    }
}
