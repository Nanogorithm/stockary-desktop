package com.stockary.common.di

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.plusAssign
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.EventBusImpl
import com.copperleaf.ballast.withViewModel
import com.stockary.common.repository.category.CategoryRepository
import com.stockary.common.repository.category.CategoryRepositoryImpl
import com.stockary.common.repository.login.LoginRepository
import com.stockary.common.repository.login.LoginRepositoryImpl
import com.stockary.common.repository.order.OrderRepository
import com.stockary.common.repository.order.OrderRepositoryImpl
import com.stockary.common.repository.product.ProductRepository
import com.stockary.common.repository.product.ProductRepositoryImpl
import com.stockary.common.ui.category.CategoryContract
import com.stockary.common.ui.category.CategoryInputHandler
import com.stockary.common.ui.category.CategoryViewModel
import com.stockary.common.ui.order.OrderContract
import com.stockary.common.ui.order.OrderInputHandler
import com.stockary.common.ui.order.OrderViewModel
import com.stockary.common.ui.product.ProductInputHandler
import com.stockary.common.ui.product.ProductViewModel
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineScope
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
            install(Postgrest) {
                // settings
            }
        }
    }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
    single<EventBus> { EventBusImpl() }
    factory<CategoryRepository> { CategoryRepositoryImpl(get(), get(), get()) }
    factory<ProductRepository> { ProductRepositoryImpl(get(), get(), get()) }
    factory<OrderRepository> { OrderRepositoryImpl(get(), get(), get()) }

    factory<BallastViewModelConfiguration.Builder> {
        BallastViewModelConfiguration.Builder().apply {
            this += LoggingInterceptor()
            logger = ::PrintlnLogger
        }
    }

    factory<CategoryInputHandler> {
        CategoryInputHandler(
            categoryRepository = get()
        )
    }

    factory<ProductInputHandler> {
        ProductInputHandler(
            productRepository = get()
        )
    }

    factory<OrderInputHandler> {
        OrderInputHandler(
            orderRepository = get()
        )
    }

    factory<CategoryViewModel> { (coroutineScope: CoroutineScope) ->
        CategoryViewModel(
            configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                inputHandler = get<CategoryInputHandler>(),
                initialState = CategoryContract.State(),
                name = "CategoryScreen",
            ), coroutineScope = coroutineScope
        )
    }

    factory<ProductViewModel> { (coroutineScope: CoroutineScope) ->
        ProductViewModel(
            configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                inputHandler = get<CategoryInputHandler>(),
                initialState = CategoryContract.State(),
                name = "CategoryScreen",
            ), coroutineScope = coroutineScope, inputHandler = get()
        )
    }

    factory<OrderViewModel> { (coroutineScope: CoroutineScope) ->
        OrderViewModel(
            configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                inputHandler = get<OrderInputHandler>(),
                initialState = OrderContract.State(),
                name = "OrderScreen",
            ), coroutineScope = coroutineScope, inputHandler = get()
        )
    }
}


// Common App Definitions
fun appModule() = listOf(commonModule, platformModule)