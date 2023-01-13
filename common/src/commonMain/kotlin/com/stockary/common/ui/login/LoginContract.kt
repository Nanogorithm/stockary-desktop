package com.stockary.common.ui.login

import io.github.jan.supabase.gotrue.SessionStatus

object LoginContract {
    data class State(
        val loading: Boolean = false,
        val error: String? = null,
        val isLoggedIn: Boolean = false
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        data class LoginByEmail(val email: String, val password: String) : Inputs()
        data class UpdateAuthStatus(val sessionStatus: SessionStatus) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
        object LoginSuccess : Events()
    }
}
