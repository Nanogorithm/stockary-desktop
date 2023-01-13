package com.stockary.common.router

import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher

// Define your routes
enum class AppScreen(
    routeFormat: String,
    val title: String,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    Home("/app/home", "Home"),
    Login("/app/login", "Login"), CustomerList(
        "/app/customers?sort={?}",
        "Customers"
    ),
    CustomerDetails(
        "/app/customers/{customerId}", "Customers Details"
    ),
    CategoryList(
        "/app/categories?sort={?}", "Categories"
    ),
    CategoryDetails("/app/categories/{categoryId}", "Category Details"), ProductList(
        "/app/products?sort={?}", "products"
    ),
    ProductDetails("/app/products/{categoryId}", "Product Details"), OrderList(
        "/app/orders?sort={?}", "Orders"
    ),
    OrderDetails("/app/orders/{orderId}", "Order Details"), ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}

val navItems = listOf(
    AppScreen.Home, AppScreen.OrderList, AppScreen.CategoryList, AppScreen.ProductList, AppScreen.CustomerList
)