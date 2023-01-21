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
import com.stockary.common.repository.product.ProductRepositoryContract
import com.stockary.common.repository.product.ProductRepositoryImpl
import com.stockary.common.repository.product.ProductRepositoryInputHandler
import com.stockary.common.router.AppScreen
import com.stockary.common.ui.category.CategoryContract
import com.stockary.common.ui.category.CategoryEventHandler
import com.stockary.common.ui.category.CategoryInputHandler
import com.stockary.common.ui.category.CategoryViewModel
import com.stockary.common.ui.new_product.NewProductContract
import com.stockary.common.ui.new_product.NewProductEventHandler
import com.stockary.common.ui.new_product.NewProductInputHandler
import com.stockary.common.ui.new_product.NewProductViewModel
import com.stockary.common.ui.product.ProductContract
import com.stockary.common.ui.product.ProductEventHandler
import com.stockary.common.ui.product.ProductInputHandler
import com.stockary.common.ui.product.ProductViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.CoroutineScope
import kotlin.system.exitProcess

class ComposeDesktopInjectorImpl(
    private val applicationScope: CoroutineScope,
) : ComposeDesktopInjector {
    @OptIn(ExperimentalBallastApi::class)
    private val router: Router<AppScreen> by lazy {
        BasicRouter(
            coroutineScope = applicationScope,
            config = BallastViewModelConfiguration.Builder().apply {
                // log all Router activity to inspect the backstack changes
                this += LoggingInterceptor()
                logger = ::PrintlnLogger
                // You may add any other Ballast Interceptors here as well, to extend the router functionality
            }.withRouter(RoutingTable.fromEnum(AppScreen.values()), initialRoute = AppScreen.Login).build(),
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

    private val eventBus = EventBusImpl()

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

    override fun newProductViewModel(coroutineScope: CoroutineScope): NewProductViewModel {
        return NewProductViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = NewProductContract.State(),
                inputHandler = NewProductInputHandler(productRepository, categoryRepository),
                name = "AddProductScreen"
            ), eventHandler = NewProductEventHandler(router)
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
