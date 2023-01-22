package com.stockary.common.repository.login

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email

class AuthRepositoryImpl(
    val supabaseClient: SupabaseClient
) : AuthRepository {
    override suspend fun loginUser(email: String, password: String) {
        try {
            val goTrue = supabaseClient.gotrue
            goTrue.loginWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun signUp(email: String, password: String) {
        try {
            val goTrue = supabaseClient.gotrue
            goTrue.signUpWith(Email) {
                this.email = email
                this.password = password
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

    override suspend fun logout() {
        try {
            supabaseClient.gotrue.invalidateSession()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun sessionStatus() = supabaseClient.gotrue.sessionStatus
}
