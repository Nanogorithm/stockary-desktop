package com.stockary.common.ui.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.*
import com.copperleaf.ballast.navigation.routing.*
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.router.AppScreen
import com.stockary.common.router.AppScreen.*
import com.stockary.common.router.navItems
import com.stockary.common.ui.category.CategoryPage
import com.stockary.common.ui.customer.CustomerPage
import com.stockary.common.ui.home.Overview
import com.stockary.common.ui.login.Login
import com.stockary.common.ui.new_category.NewCategoryPage
import com.stockary.common.ui.new_order.NewOrder
import com.stockary.common.ui.new_product.NewProductPage
import com.stockary.common.ui.order.OrderPage
import com.stockary.common.ui.product.ProductPage
import com.stockary.common.ui.summary.Summary
import io.github.jan.supabase.gotrue.SessionStatus
import org.koin.core.component.KoinComponent


class AppScreenView(
    val injector: ComposeDesktopInjector
) : KoinComponent {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun App() {
        val viewModelScope = rememberCoroutineScope()
        val router = remember(injector) { injector.router() }

        val vm = remember(viewModelScope) { injector.appViewModel(viewModelScope) }

        val uiState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(AppContract.Inputs.Initialize)
        }

        // collect the Router's StateFlow as a Compose State
        val routerState: Backstack<AppScreen> by router.observeStates().collectAsState()

        if (uiState.loading) {
            //show splash screen
            val logoAnimation by animateFloatAsState(2f)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.height(40.dp).padding(start = 40.dp)) {
                    Image(painter = painterResource("images/stockary_logo.png"), null)
                }
            }
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize()
            ) { innerPaddings ->
                PermanentNavigationDrawer(
                    drawerContent = {
                        Spacer(modifier = Modifier.height(48.dp))
                        Box(modifier = Modifier.height(40.dp).padding(start = 40.dp)) {
                            Image(painter = painterResource("images/stockary_logo.png"), null)
                        }
                        Spacer(Modifier.height(48.dp))
                        navItems.forEach { item ->
                            NavigationDrawerItem(
                                icon = {
                                    item.icon?.let {
                                        Icon(it, contentDescription = null)
                                    }
                                },
                                label = { Text(item.title) },
                                selected = routerState.currentRouteOrNull == item,
                                onClick = {
                                    router.trySend(
                                        RouterContract.Inputs.GoToDestination(
                                            item.directions().build()
                                        )
                                    )
                                },
                                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = Color(0xFFD6E2F8),
                                    unselectedContainerColor = Color.Transparent
                                )
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        NavigationDrawerItem(
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            label = { Text("Settings") },
                            selected = false,
                            onClick = {

                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = Color(0xFFD6E2F8), unselectedContainerColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.height(56.dp))
                    },
                    modifier = Modifier.padding(innerPaddings),
                    drawerContainerColor = Color(0xFFF0F6FF),
                    drawerContentColor = contentColorFor(Color(0xFFF0F6FF))
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(48.dp))
                        Box(
                            modifier = Modifier.padding(end = 50.dp).align(Alignment.End),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            var expandMenu by remember { mutableStateOf(false) }

                            when (uiState.sessionStatus) {
                                is SessionStatus.Authenticated, SessionStatus.LoadingFromStorage -> {
                                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).clickable {
                                        expandMenu = true
                                    }) {
                                        Box(
                                            modifier = Modifier.size(24.dp).clip(CircleShape)
                                                .background(Color(0xFFE2E3FF)), contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.Person, null)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Shaad & Co", fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(Icons.Default.ArrowDropDown, null)
                                    }
                                }

                                SessionStatus.NetworkError -> {}
                                SessionStatus.NotAuthenticated -> {}
                            }

                            DropdownMenu(expanded = expandMenu, onDismissRequest = {
                                expandMenu = false
                            }) {
                                DropdownMenuItem(onClick = {
                                    expandMenu = false
                                    vm.trySend(AppContract.Inputs.Logout)
                                }) {
                                    Text("Logout")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(48.dp))
                        Box(modifier = Modifier.fillMaxSize()) {
                            routerState.renderCurrentDestination(
                                route = { appScreen ->
                                    // the last entry in the backstack was matched to a route. We will switch on which route was matched,
                                    // and pull path and query parameters from the destination
                                    when (appScreen) {
                                        Login -> {
                                            Login(router)
                                        }

                                        Home -> {
                                            Overview(injector)
                                        }

                                        CategoryList -> {
                                            val sort: String? by optionalStringQuery()
                                            CategoryPage().Categories(injector)
                                        }

                                        CategoryDetails -> {
                                            val categoryId: Long by longPath()
                                        }

                                        NewCategory -> {
                                            NewCategoryPage().NewCategory(injector = injector)
                                        }

                                        CustomerList -> {
                                            CustomerPage().Customer(injector)
                                        }

                                        CustomerDetails -> {

                                        }

                                        ProductList -> {
                                            ProductPage().Product(injector = injector)
                                        }

                                        NewProduct -> {
                                            val productId: Int? by optionalIntQuery()
                                            NewProductPage().NewProduct(injector = injector, productId)
                                        }

                                        ProductDetails -> {

                                        }

                                        OrderList -> {
                                            OrderPage().Orders(injector = injector)
                                        }

                                        OrderDetails -> {

                                        }

                                        NewOrder -> {
                                            NewOrder()
                                        }

                                        OrderSummary -> {
                                            Summary(injector)
                                        }

                                        NewCustomer -> {
                                            val customerId: Int? by optionalIntQuery()
//                                            NewCustomerPage().NewCustomer(injector,customerId)
                                        }
                                    }
                                },
                                notFound = {
                                    // the last entry in the backstack could not be matched to a route
                                    Text("Not found")
                                },
                            )
                        }
                    }
                }
            }
        }

    }
}


