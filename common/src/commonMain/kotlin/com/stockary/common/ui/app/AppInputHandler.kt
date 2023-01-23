package com.stockary.common.ui.app

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.login.AuthRepository
import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.map

class AppInputHandler(
    private val authRepository: AuthRepository
) : InputHandler<
        AppContract.Inputs,
        AppContract.Events,
        AppContract.State> {
    override suspend fun InputHandlerScope<
            AppContract.Inputs,
            AppContract.Events,
            AppContract.State>.handleInput(
        input: AppContract.Inputs
    ) = when (input) {
        is AppContract.Inputs.Initialize -> {
            postInput(AppContract.Inputs.GetSessionStatus)
        }

        is AppContract.Inputs.GoBack -> {
            postEvent(AppContract.Events.NavigateUp)
        }

        AppContract.Inputs.GetSessionStatus -> {
            observeFlows("GetSessionStatus") {
                listOf(
                    authRepository.sessionStatus().map { AppContract.Inputs.UpdateSessionStatus(it) }
                )
            }
        }

        is AppContract.Inputs.UpdateSessionStatus -> {
            when(input.sessionStatus){
                is SessionStatus.Authenticated -> {

                }
                SessionStatus.LoadingFromStorage -> {

                }
                SessionStatus.NetworkError -> {
                    postEvent(AppContract.Events.LoginScreen)
                }
                SessionStatus.NotAuthenticated -> {
                    postEvent(AppContract.Events.LoginScreen)
                }
            }
            updateState { it.copy(loading = false, sessionStatus = input.sessionStatus) }
        }

        AppContract.Inputs.Logout -> {
            authRepository.logout()
        }
    }
}
