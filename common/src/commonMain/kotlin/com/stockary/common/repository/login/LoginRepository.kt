package com.stockary.common.repository.login

import io.github.jan.supabase.gotrue.SessionStatus
import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    suspend fun loginUser(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    fun sessionStatus(): Flow<SessionStatus>
}
