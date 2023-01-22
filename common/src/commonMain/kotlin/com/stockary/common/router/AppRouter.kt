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
    Login("/app/login", "Login"),
    CustomerList(
        "/app/customers?sort={?}", "Customers"
    ),
    NewCustomer("/app/new-customers", "New Customer"),
    CustomerDetails(
        "/app/customers/{customerId}", "Customers Details"
    ),
    NewCategory(
        "/app/new-category", "Add Category"
    ),
    CategoryList(
        "/app/categories?sort={?}", "Categories"
    ),
    CategoryDetails("/app/categories/{categoryId}", "Category Details"),

    ProductList(
        "/app/products?sort={?}", "products"
    ),
    NewProduct(
        "/app/new-product", "Add Product"
    ),
    ProductDetails("/app/products/{categoryId}", "Product Details"),
    NewOrder(
        "/app/new-order", "New Order"
    ),
    OrderList(
        "/app/orders?sort={?}", "Orders"
    ),
    OrderDetails("/app/orders/{orderId}", "Order Details"), OrderSummary("/app/summary", "Order Summary"), ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}

val navItems = listOf(
    AppScreen.Home, AppScreen.ProductList, AppScreen.CustomerList, AppScreen.OrderList,
)