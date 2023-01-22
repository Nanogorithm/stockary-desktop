package com.stockary.common.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shop2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector
import com.copperleaf.ballast.navigation.routing.Route
import com.copperleaf.ballast.navigation.routing.RouteAnnotation
import com.copperleaf.ballast.navigation.routing.RouteMatcher

// Define your routes
enum class AppScreen(
    routeFormat: String,
    val title: String,
    val icon: ImageVector? = null,
    override val annotations: Set<RouteAnnotation> = emptySet(),
) : Route {
    Home("/app/home", "Home", icon = Icons.Default.Book),
    Login("/app/login", "Login"),
    CustomerList(
        "/app/customers?sort={?}", "Customers", icon = Icons.Default.Person
    ),
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
        "/app/products?sort={?}", "products", icon = Icons.Default.ShoppingCart
    ),
    NewProduct(
        "/app/new-product", "Add Product"
    ),
    ProductDetails("/app/products/{categoryId}", "Product Details"), NewOrder(
        "/app/orders?sort={?}", "Orders"
    ),
    OrderList(
        "/app/orders?sort={?}", "Orders", icon = Icons.Default.Shop2
    ),
    OrderDetails("/app/orders/{orderId}", "Order Details"), OrderSummary("/app/summary", "Order Summary"), ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}

val navItems = listOf(
    AppScreen.Home, AppScreen.ProductList, AppScreen.CustomerList, AppScreen.OrderList,
)