package com.stockary.common.repository.product.model

import com.stockary.common.repository.category.model.Category
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int? = null,
    val title: String,
    val description: String? = null,
    val stock: Int = 0,
    val sort: Int = 0,
    @SerialName("unit_amount") val unitAmount: Float = 0f,
    @SerialName("category_id") val categoryId: Int? = null,
    @SerialName("unit_type_id") val unitTypeId: Int? = null,

    val photo: String? = null,

    @SerialName("categories") var category: Category? = null,
    @SerialName("unit_types") var unitType: UnitType? = null
)

@Serializable
data class ProductCustomerRole(
    val id: Int? = null, val product_id: Int, val customer_role_id: Int, val price: Float
)

@Serializable
data class UnitType(
    val id: Int? = null, val name: String
)