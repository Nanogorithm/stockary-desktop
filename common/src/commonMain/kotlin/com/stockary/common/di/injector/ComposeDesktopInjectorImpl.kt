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
import com.stockary.common.repository.order.OrderRepositoryContract
import com.stockary.common.repository.order.OrderRepositoryImpl
import com.stockary.common.repository.order.OrderRepositoryInputHandler
import com.stockary.common.repository.product.ProductRepositoryContract
import com.stockary.common.repository.product.ProductRepositoryImpl
import com.stockary.common.repository.product.ProductRepositoryInputHandler
import com.stockary.common.repository.type.CustomerTypeRepositoryContract
import com.stockary.common.repository.type.CustomerTypeRepositoryImpl
import com.stockary.common.repository.type.CustomerTypeRepositoryInputHandler
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
import com.stockary.common.ui.home.HomeContract
import com.stockary.common.ui.home.HomeEventHandler
import com.stockary.common.ui.home.HomeInputHandler
import com.stockary.common.ui.home.HomeViewModel
import com.stockary.common.ui.new_category.NewCategoryContract
import com.stockary.common.ui.new_category.NewCategoryEventHandler
import com.stockary.common.ui.new_category.NewCategoryInputHandler
import com.stockary.common.ui.new_category.NewCategoryViewModel
import com.stockary.common.ui.new_customer.NewCustomerContract
import com.stockary.common.ui.new_customer.NewCustomerEventHandler
import com.stockary.common.ui.new_customer.NewCustomerInputHandler
import com.stockary.common.ui.new_customer.NewCustomerViewModel
import com.stockary.common.ui.new_product.NewProductContract
import com.stockary.common.ui.new_product.NewProductEventHandler
import com.stockary.common.ui.new_product.NewProductInputHandler
import com.stockary.common.ui.new_product.NewProductViewModel
import com.stockary.common.ui.new_types.NewCustomerTypeContract
import com.stockary.common.ui.new_types.NewCustomerTypeEventHandler
import com.stockary.common.ui.new_types.NewCustomerTypeInputHandler
import com.stockary.common.ui.new_types.NewCustomerTypeViewModel
import com.stockary.common.ui.order.OrderContract
import com.stockary.common.ui.order.OrderEventHandler
import com.stockary.common.ui.order.OrderInputHandler
import com.stockary.common.ui.order.OrderViewModel
import com.stockary.common.ui.product.ProductContract
import com.stockary.common.ui.product.ProductEventHandler
import com.stockary.common.ui.product.ProductInputHandler
import com.stockary.common.ui.product.ProductViewModel
import com.stockary.common.ui.summary.SummaryContract
import com.stockary.common.ui.summary.SummaryEventHandler
import com.stockary.common.ui.summary.SummaryInputHandler
import com.stockary.common.ui.summary.SummaryViewModel
import com.stockary.common.ui.types.TypeContract
import com.stockary.common.ui.types.TypeEventHandler
import com.stockary.common.ui.types.TypeInputHandler
import com.stockary.common.ui.types.TypeViewModel
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

    override fun homeViewModel(coroutineScope: CoroutineScope): HomeViewModel {
        return HomeViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                inputHandler = HomeInputHandler(orderRepository = orderRepository),
                initialState = HomeContract.State(),
                name = "HomeScreen",
            ), eventHandler = HomeEventHandler(router)
        )
    }

    override fun orderViewModel(coroutineScope: CoroutineScope): OrderViewModel {
        return OrderViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = OrderContract.State(),
                inputHandler = OrderInputHandler(orderRepository = orderRepository),
                name = "Order Screen"
            ), eventHandler = OrderEventHandler(router)
        )
    }

    override fun summaryViewModel(coroutineScope: CoroutineScope): SummaryViewModel {
        return SummaryViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                inputHandler = SummaryInputHandler(orderRepository = orderRepository),
                initialState = SummaryContract.State(),
                name = "SummaryScreen",
            ), eventHandler = SummaryEventHandler(router)
        )
    }

    override fun typeViewModel(coroutineScope: CoroutineScope): TypeViewModel {
        return TypeViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                inputHandler = TypeInputHandler(customerTypeRepository = customerTypeRepository),
                initialState = TypeContract.State(),
                name = "SummaryScreen",
            ), eventHandler = TypeEventHandler(router)
        )
    }

    private val eventBus = EventBusImpl()

    private val authRepository by lazy {
        AuthRepositoryImpl(supabaseClient = get())
    }

    private val orderRepository by lazy {
        OrderRepositoryImpl(
            coroutineScope = applicationScope,
            eventBus = eventBus,
            configBuilder = commonBuilder().withViewModel(
                inputHandler = OrderRepositoryInputHandler(
                    eventBus = eventBus,
                ),
                initialState = OrderRepositoryContract.State(),
                name = "Order Repository",
            ).withRepository(),
        )
    }

    private val customerTypeRepository by lazy {
        CustomerTypeRepositoryImpl(
            coroutineScope = applicationScope,
            eventBus = eventBus,
            configBuilder = commonBuilder().withViewModel(
                    inputHandler = CustomerTypeRepositoryInputHandler(
                        eventBus = eventBus,
                    ),
                    initialState = CustomerTypeRepositoryContract.State(),
                    name = "Customer Type Repository",
                ).withRepository(),
        )
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
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = NewCategoryContract.State(),
                inputHandler = NewCategoryInputHandler(categoryRepository),
                name = "NewCategoryScreen"
            ), eventHandler = NewCategoryEventHandler(router)
        )
    }

    override fun newCustomerViewModel(coroutineScope: CoroutineScope): NewCustomerViewModel {
        return NewCustomerViewModel(
            coroutineScope = coroutineScope, configBuilder = commonBuilder().withViewModel(
                initialState = NewCustomerContract.State(),
                inputHandler = NewCustomerInputHandler(customerRepository),
                name = "NewCustomerScreen"
            ), eventHandler = NewCustomerEventHandler(router)
        )
    }

    override fun newCustomerTypeViewModel(coroutineScope: CoroutineScope): NewCustomerTypeViewModel {
        return NewCustomerTypeViewModel(
            coroutineScope = coroutineScope,
            configBuilder = commonBuilder().withViewModel(
                    initialState = NewCustomerTypeContract.State(),
                    inputHandler = NewCustomerTypeInputHandler(customerTypeRepository),
                    name = "NewCustomerTypeScreen"
                ),
            eventHandler = NewCustomerTypeEventHandler(router)
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
