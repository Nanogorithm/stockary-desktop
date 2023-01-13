package com.stockary.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
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
import com.copperleaf.ballast.core.LoggingInterceptor
import com.copperleaf.ballast.core.PrintlnLogger
import com.copperleaf.ballast.navigation.routing.*
import com.copperleaf.ballast.navigation.vm.BasicRouter
import com.copperleaf.ballast.navigation.vm.Router
import com.copperleaf.ballast.navigation.vm.withRouter
import com.stockary.common.router.AppScreen
import com.stockary.common.router.AppScreen.*
import com.stockary.common.router.navItems
import com.stockary.common.screen.Customer
import com.stockary.common.screen.NewCategory
import com.stockary.common.screen.NewProduct
import com.stockary.common.screen.Overview
import com.stockary.common.ui.category.CategoryPage
import com.stockary.common.ui.login.Login
import com.stockary.common.ui.order.OrderPage
import com.stockary.common.ui.product.ProductPage
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class, ExperimentalBallastApi::class)
@Composable
fun App() {

    val applicationScope = rememberCoroutineScope()

    // Set up the Router, which is just a normal Ballast ViewModel
    val router: Router<AppScreen> = remember(applicationScope) {
        BasicRouter(
            coroutineScope = applicationScope,
            config = BallastViewModelConfiguration.Builder().apply {
                // log all Router activity to inspect the backstack changes
                this += LoggingInterceptor()
                logger = ::PrintlnLogger

                // You may add any other Ballast Interceptors here as well, to extend the router functionality
            }.withRouter(RoutingTable.fromEnum(values()), initialRoute = Login).build(),
            eventHandler = eventHandler {
                if (it is RouterContract.Events.BackstackEmptied) {
                    exitProcess(0)
                }
            },
        )
    }

    // collect the Router's StateFlow as a Compose State
    val routerState: Backstack<AppScreen> by router.observeStates().collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
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
                            selectedContainerColor = Color(0xFFD6E2F8), unselectedContainerColor = Color.Transparent
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
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(end = 50.dp)
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFE2E3FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Shaad & Co", fontSize = 14.sp)
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
                                    Overview()
                                }

                                CategoryList -> {
                                    val sort: String? by optionalStringQuery()
                                    CategoryPage().Categories()
                                    //
                                    /*  PostListScreen(
                                              sort = sort,
                                              onPostSelected = { postId: Long ->
                                                  // The user selected a post within the PostListScreen. Generate a URL which will match
                                                  // to the PostDetails route, by using its directions to ensure the right parameters are
                                                  // provided in the URL
                                                  router.trySend(
                                                      RouterContract.Inputs.GoToDestination(
                                                          AppScreen.CategoryDetails
                                                              .directions()
                                                              .pathParameter("categoryId", postId.toString())
                                                              .build()
                                                      )
                                                  )
                                              },
                                          )*/
                                }

                                CategoryDetails -> {
                                    val categoryId: Long by longPath()
                                    NewCategory()
                                }

                                CustomerList -> {
                                    Customer()
                                }

                                CustomerDetails -> {

                                }

                                ProductList -> {
                                    ProductPage().Product()
                                }

                                ProductDetails -> {
                                    NewProduct()
                                }

                                OrderList -> {
                                    OrderPage().Orders()
                                }

                                OrderDetails -> {

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
