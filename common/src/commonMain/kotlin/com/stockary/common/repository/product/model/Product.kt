package com.stockary.common.repository.product.model

import kotlinx.serialization.Serializable

@Serializable
data class Product(
    var id: String? = null,
    val title: String = "",
    val description: String? = null,
    val stock: Int = 0,
    val sort: Int = 0,
    val unitAmount: Float = 0f,
    val categoryId: Int? = null,
    val unitType: String? = null,

    val photo: String? = null,
    val code: String? = null,
    val prices: Map<String, Double>? = null,
    val units: Units? = null,

    var category: String? = null,
    val productCustomerRole: List<ProductCustomerRole> = emptyList()
)

@Serializable
data class Price(
    val dealer: Double = 0.0, val customer: Double = 0.0
)

@Serializable
data class Units(
    val amount: Float = 0f, val type: String? = null
)

@Serializable
data class ProductCustomerRole(
    val id: Int? = null, val product_id: Int, val customer_role_id: Int, val price: Float
)

@Serializable
data class UnitType(
    val id: Int? = null, val name: String
)