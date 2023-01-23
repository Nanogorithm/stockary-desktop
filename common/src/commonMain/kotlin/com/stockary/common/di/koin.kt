package com.stockary.common.di

import com.copperleaf.ballast.*
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.EventBusImpl
import com.stockary.common.repository.login.AuthRepository
import com.stockary.common.repository.login.AuthRepositoryImpl
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import org.koin.dsl.module


// platform Module
val platformModule = module {

}

// platform Module
val commonModule = module {
    single {
        Napier.base(DebugAntilog())
        createSupabaseClient(
            supabaseUrl = "https://nfwwajxqeilqdkvwfojz.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5md3dhanhxZWlscWRrdndmb2p6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2NzM1MTQ3NTEsImV4cCI6MTk4OTA5MDc1MX0.ZJawzRdHU9kRG5UBoRdyXPwlq6cinHhj1ItGKQGOsG4"
        ) {
            install(GoTrue)
            install(Postgrest) {
                // settings
            }
            install(Storage) {
                // settings
            }
        }
    }

    single<EventBus> { EventBusImpl() }

    single<AuthRepository> { AuthRepositoryImpl(get()) }

    factory<BallastViewModelConfiguration.Builder> {
        BallastViewModelConfiguration.Builder().apply {
            this += LoggingInterceptor()
            logger = ::PrintlnLogger
        }
    }
}


// Common App Definitions
fun appModule() = listOf(commonModule, platformModule)