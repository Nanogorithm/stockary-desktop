package com.stockary.common.di.injector

import com.copperleaf.ballast.*
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.debugger.BallastDebuggerClientConnection
import com.copperleaf.ballast.debugger.BallastDebuggerInterceptor
import com.copperleaf.ballast.navigation.routing.RouterContract
import com.copperleaf.ballast.navigation.routing.RoutingTable
import com.copperleaf.ballast.navigation.routing.fromEnum
import com.copperleaf.ballast.navigation.vm.BasicRouter
import com.copperleaf.ballast.navigation.vm.Router
import com.copperleaf.ballast.navigation.vm.withRouter
import com.copperleaf.ballast.repository.bus.EventBusImpl
import com.copperleaf.ballast.repository.withRepository
import com.stockary.common.repository.category.CategoryRepositoryContract
import com.stockary.common.repository.category.CategoryRepositoryImpl
import com.stockary.common.repository.category.CategoryRepositoryInputHandler
import com.stockary.common.repository.customer.CustomerRepositoryContract
import com.stockary.common.repository.customer.CustomerRepositoryImpl
import com.stockary.common.repository.customer.CustomerRepositoryInputHandler
import com.stockary.common.repository.login.AuthRepositoryImpl
import com.stockary.common.repository.product.ProductRepositoryContract
import com.stockary.common.repository.product.ProductRepositoryImpl
import com.stockary.common.repository.product.ProductRepositoryInputHandler
import com.stockary.common.router.AppScreen
import com.stockary.common.ui.app.AppContract
import com.stockary.common.ui.app.AppEventHandler
import com.stockary.common.ui.app.AppInputHandler
import com.stockary.common.ui.app.AppViewModel
import com.stockary.common.ui.category.CategoryContract
import com.stockary.common.ui.category.CategoryEventHandler
import com.stockary.common.ui.category.CategoryInputHandler
import com.stockary.common.ui.category.CategoryViewModel
import com.stockary.common.ui.customer.CustomerContract
import com.stockary.common.ui.customer.CustomerEventHandler
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
import com.stockary.common.ui.product.ProductContract
import com.stockary.common.ui.product.ProductEventHandler
import com.stockary.common.ui.product.ProductInputHandler
import com.stockary.common.ui.product.ProductViewModel
import io.github.jan.supabase.SupabaseClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.system.exitProcess

class ComposeDesktopInjectorImpl(
    private val applicationScope: CoroutineScope,
) : ComposeDesktopInjector, KoinComponent {
    @OptIn(ExperimentalBallastApi::class)
    private val router: Router<AppScreen> by lazy {
        BasicRouter(
            coroutineScope = applicationScope,
            config = BallastViewModelConfiguration.Builder().apply {
                // log all Router activity to inspect the backstack changes
                this += LoggingInterceptor()
                logger = ::PrintlnLogger
                // You may add any other Ballast Interceptors here as well, to extend the router functionality
            }.withRouter(RoutingTable.fromEnum(AppScreen.values()), initialRoute = AppScreen.Home).build(),
            eventHandler = eventHandler {
                if (it is RouterContract.Events.BackstackEmptied) {
                    exitProcess(0)
                }
            },
        )
    }

    override fun router(): Router<AppScreen> {
        return router
    }

    override fun appViewModel(coroutineScope: CoroutineScope): AppViewModel {
        return AppViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = AppContract.State(),
                inputHandler = AppInputHandler(authRepository = authRepository),
                name = "AppScreen"
            ), eventHandler = AppEventHandler(router)
        )
    }

    private val eventBus = EventBusImpl()

    private val authRepository by lazy {
        AuthRepositoryImpl(supabaseClient = get())
    }

    private val categoryRepository by lazy {
        CategoryRepositoryImpl(
            coroutineScope = applicationScope,
            eventBus = eventBus,
            configBuilder = commonBuilder().withViewModel(
                inputHandler = CategoryRepositoryInputHandler(
                    eventBus = eventBus,
                ),
                initialState = CategoryRepositoryContract.State(),
                name = "Category Repository",
            ).withRepository(),
        )
    }

    private val productRepository by lazy {
        ProductRepositoryImpl(
            coroutineScope = applicationScope,
            eventBus = eventBus,
            configBuilder = commonBuilder().withViewModel(
                inputHandler = ProductRepositoryInputHandler(
                    eventBus = eventBus,
                ),
                initialState = ProductRepositoryContract.State(),
                name = "Product Repository",
            ).withRepository(),
        )
    }

    private val customerRepository by lazy {
        CustomerRepositoryImpl(
            coroutineScope = applicationScope,
            eventBus = eventBus,
            configBuilder = commonBuilder().withViewModel(
                inputHandler = CustomerRepositoryInputHandler(
                    eventBus = eventBus,
                ),
                initialState = CustomerRepositoryContract.State(),
                name = "Customer Repository",
            ).withRepository(),
        )
    }

    override fun categoryViewModel(coroutineScope: CoroutineScope): CategoryViewModel {
        return CategoryViewModel(
            coroutineScope = coroutineScope,
            configBuilder = commonBuilder().withViewModel(
                initialState = CategoryContract.State(),
                inputHandler = CategoryInputHandler(categoryRepository),
                name = "CategoryScreen",
            ),
            eventHandler = CategoryEventHandler(router),
        )
    }

    override fun productViewModel(coroutineScope: CoroutineScope): ProductViewModel {
        return ProductViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = ProductContract.State(),
                inputHandler = ProductInputHandler(productRepository),
                name = "ProductScreen",
            ), eventHandler = ProductEventHandler(router)
        )
    }

    override fun customerViewModel(coroutineScope: CoroutineScope): CustomerViewModel {
        return CustomerViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = CustomerContract.State(),
                inputHandler = CustomerInputHandler(customerRepository = customerRepository),
                name = "CustomersScreen"
            ), eventHandler = CustomerEventHandler(router)
        )
    }

    override fun newProductViewModel(coroutineScope: CoroutineScope): NewProductViewModel {
        return NewProductViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = NewProductContract.State(),
                inputHandler = NewProductInputHandler(productRepository, categoryRepository),
                name = "AddProductScreen"
            ), eventHandler = NewProductEventHandler(router)
        )
    }

    override fun newCategoryViewModel(coroutineScope: CoroutineScope): NewCategoryViewModel {
        return NewCategoryViewModel(
            coroutineScope = coroutineScope,
            configBuilder = commonBuilder().withViewModel(
                initialState = NewCategoryContract.State(),
                inputHandler = NewCategoryInputHandler(categoryRepository),
                name = "NewCategoryScreen"
            ),
            eventHandler = NewCategoryEventHandler(router)
        )
    }

    // configs
// ---------------------------------------------------------------------------------------------------------------------
    private val debuggerConnection by lazy {
        BallastDebuggerClientConnection(
            engineFactory = CIO,
            applicationCoroutineScope = applicationScope,
            host = "127.0.0.1",
        ).also {
            it.connect()
        }
    }

    private fun commonBuilder(): BallastViewModelConfiguration.Builder {
        return BallastViewModelConfiguration.Builder().apply {
            this += LoggingInterceptor()
            this += BallastDebuggerInterceptor(debuggerConnection)
            logger = ::PrintlnLogger
        }
    }
}
