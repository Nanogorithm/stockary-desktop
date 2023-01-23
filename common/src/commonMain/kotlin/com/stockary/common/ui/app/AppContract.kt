package com.stockary.common.ui.app

import io.github.jan.supabase.gotrue.SessionStatus

object AppContract {
    data class State(
        val loading: Boolean = true,
        val sessionStatus: SessionStatus = SessionStatus.NotAuthenticated
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        object Logout : Inputs()
        object GetSessionStatus : Inputs()
        data class UpdateSessionStatus(val sessionStatus: SessionStatus) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
        object LoginScreen : Events()
    }
}
