package com.stockary.common.di

import com.copperleaf.ballast.*
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.navigation.vm.Router
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.EventBusImpl
import com.stockary.common.repository.customer.CustomerRepository
import com.stockary.common.repository.customer.CustomerRepositoryImpl
import com.stockary.common.repository.login.LoginRepository
import com.stockary.common.repository.login.LoginRepositoryImpl
import com.stockary.common.repository.order.OrderRepository
import com.stockary.common.repository.order.OrderRepositoryImpl
import com.stockary.common.router.AppScreen
import com.stockary.common.ui.customer.CustomerContract
import com.stockary.common.ui.customer.CustomerInputHandler
import com.stockary.common.ui.customer.CustomerViewModel
import com.stockary.common.ui.new_category.NewCategoryContract
import com.stockary.common.ui.new_category.NewCategoryEventHandler
import com.stockary.common.ui.new_category.NewCategoryInputHandler
import com.stockary.common.ui.new_category.NewCategoryViewModel
import com.stockary.common.ui.new_product.NewProductContract
import com.stockary.common.ui.new_product.NewProductEventHandler
import com.stockary.common.ui.new_product.NewProductInputHandler
import com.stockary.common.ui.new_product.NewProductViewModel
import com.stockary.common.ui.order.OrderContract
import com.stockary.common.ui.order.OrderInputHandler
import com.stockary.common.ui.order.OrderViewModel
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module


// platform Module
val platformModule = module {

}

// platform Module
@OptIn(ExperimentalBallastApi::class)
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

    single<EventBus> { EventBusImpl() }

    factory<LoginRepository> { LoginRepositoryImpl(get()) }

//    single<CategoryRepository> { CategoryRepositoryImpl(get(), get(), get()) }
//    single<ProductRepository> { ProductRepositoryImpl(get(), get(), get()) }

    factory<OrderRepository> { OrderRepositoryImpl(get(), get(), get()) }
    factory<CustomerRepository> { CustomerRepositoryImpl(get(), get(), get()) }

    factory<BallastViewModelConfiguration.Builder> {
        BallastViewModelConfiguration.Builder().apply {
            this += LoggingInterceptor()
            logger = ::PrintlnLogger
        }
    }

    /*factory<CategoryInputHandler> {
        CategoryInputHandler(
            categoryRepository = get()
        )
    }
    factory<CategoryEventHandler> { CategoryEventHandler(get()) }*/

    factory<NewCategoryInputHandler> {
        NewCategoryInputHandler(
            categoryRepository = get()
        )
    }
    factory<NewCategoryEventHandler> { NewCategoryEventHandler(get()) }

    /*factory<ProductInputHandler> {
        ProductInputHandler(
            productRepository = get()
        )
    }*/

    factory<NewProductInputHandler> {
        NewProductInputHandler(
            productRepository = get(), categoryRepository = get()
        )
    }

    factory<NewProductEventHandler> { NewProductEventHandler(get()) }

    /*
        factory<ProductEventHandler> { ProductEventHandler(get()) }*/


    factory<OrderInputHandler> {
        OrderInputHandler(
            orderRepository = get()
        )
    }

    factory<CustomerInputHandler> {
        CustomerInputHandler(
            customerRepository = get()
        )
    }

    /*    factory<CategoryViewModel> { (coroutineScope: CoroutineScope) ->
            CategoryViewModel(
                configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                    inputHandler = get<CategoryInputHandler>(),
                    initialState = CategoryContract.State(),
                    name = "CategoryScreen",
                ), coroutineScope = coroutineScope, eventHandler = get()
            )
        }*/

    factory<NewCategoryViewModel> { (coroutineScope: CoroutineScope, router: Router<AppScreen>) ->
        NewCategoryViewModel(
            configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                inputHandler = get<NewCategoryInputHandler>(),
                initialState = NewCategoryContract.State(),
                name = "AddCategory",
            ), coroutineScope = coroutineScope, eventHandler = get<NewCategoryEventHandler>()
        )
    }

    /*    factory<ProductViewModel> { (coroutineScope: CoroutineScope) ->
            ProductViewModel(
                configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                    inputHandler = get<ProductInputHandler>(),
                    initialState = ProductContract.State(),
                    name = "ProductScreen",
                ), coroutineScope = coroutineScope, eventHandler = get()
            )
        }*/

    factory<NewProductViewModel> { (coroutineScope: CoroutineScope, router: Router<AppScreen>) ->
        NewProductViewModel(
            configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                inputHandler = get<NewProductInputHandler>(),
                initialState = NewProductContract.State(),
                name = "NewProduct",
            ), coroutineScope = coroutineScope, eventHandler = get()
        )
    }

    factory<OrderViewModel> { (coroutineScope: CoroutineScope) ->
        OrderViewModel(
            configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                inputHandler = get<OrderInputHandler>(),
                initialState = OrderContract.State(),
                name = "OrderScreen",
            ), coroutineScope = coroutineScope
        )
    }

    factory<CustomerViewModel> { (coroutineScope: CoroutineScope) ->
        CustomerViewModel(
            configBuilder = get<BallastViewModelConfiguration.Builder>().withViewModel(
                inputHandler = get<CustomerInputHandler>(),
                initialState = CustomerContract.State(),
                name = "CustomerScreen",
            ), coroutineScope = coroutineScope
        )
    }
}


// Common App Definitions
fun appModule() = listOf(commonModule, platformModule)