package com.stockary.common.di

import com.stockary.common.repository.login.LoginRepository
import com.stockary.common.repository.login.LoginRepositoryImpl
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import org.koin.dsl.module


// platform Module
val platformModule = module {

}

// platform Module
val commonModule = module {
    single {
        createSupabaseClient(
            supabaseUrl = "https://nfwwajxqeilqdkvwfojz.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5md3dhanhxZWlscWRrdndmb2p6Iiwicm9sZSI6ImFub24iLCJpYXQiOjE2NzM1MTQ3NTEsImV4cCI6MTk4OTA5MDc1MX0.ZJawzRdHU9kRG5UBoRdyXPwlq6cinHhj1ItGKQGOsG4"
        ) {
            install(GoTrue)
        }
    }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
}


// Common App Definitions
fun appModule() = listOf(commonModule, platformModule)