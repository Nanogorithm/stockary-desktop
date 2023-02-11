package com.stockary.common.di

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.EventBusImpl
import com.stockary.common.repository.login.AuthRepository
import com.stockary.common.repository.login.AuthRepositoryImpl
import io.appwrite.Client
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
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5md3dhanhxZWlscWRrdndmb2p6Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY3MzUxNDc1MSwiZXhwIjoxOTg5MDkwNzUxfQ.VqIPOoipJOmqylpBWMvjeHpbVCZAPiipTJB2DpAa1XE"
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

    single {
        val client = Client().setEndpoint("https://stockary.co/v1") // Your API Endpoint
            .setProject("63e66342df8b52835bb2") // Your project ID
            .setKey("7da6913354ddfdb457552d751c6d763d052d20c79bdc6dd05a858b89c6a1766fb3f46419c7f9833c0b99871644b6f05ce742f43d57dc37111bb423a71ab0af936b27c5e43157ef10b969be899d154c507752ef482f80c0c5d02567354dcd56aba385a02e757e0b9e259859090a767a9e4a69b338287d649f384a33424e57e148") // Your secret API key
        client
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