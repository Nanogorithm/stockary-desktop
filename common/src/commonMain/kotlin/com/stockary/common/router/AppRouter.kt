package com.stockary.common.router

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    Login("/app/login", "Login"),
    Home("/app/home", "Home", icon = Icons.Default.Book),
    CustomerList(
        "/app/customers?sort={?}", "Customers", icon = Icons.Default.Person
    ),
    NewCustomer("/app/new-customers?customerId={?}", "New Customer"),
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
    CustomerTypeList(
        "/app/customer-types?sort={?}", "Customer types"
    ),
    NewCustomerType(
        "/app/new-customer-type?typeId={?}", "Add Customer Type"
    ),
    ProductList(
        "/app/products?sort={?}", "Products", icon = Icons.Default.ShoppingCart
    ),
    NewProduct(
        "/app/new-product?productId={?}", "Add Product"
    ),
    ProductDetails("/app/products/{categoryId}", "Product Details"),
    NewOrder(
        "/app/new-order", "New Order"
    ),
    OrderList(
        "/app/orders?sort={?}", "Orders", icon = Icons.Default.Shop2
    ),
    OrderDetails("/app/orders/{orderId}", "Order Details"),
    OrderSummary(
        "/app/summary", "Order Summary", icon = Icons.Default.BarChart
    ), ;

    override val matcher: RouteMatcher = RouteMatcher.create(routeFormat)
}

val navItems = listOf(
    AppScreen.Home, AppScreen.ProductList, AppScreen.CustomerList, AppScreen.OrderList, AppScreen.OrderSummary
)