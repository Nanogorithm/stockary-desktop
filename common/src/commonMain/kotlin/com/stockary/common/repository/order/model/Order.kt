package com.stockary.common.repository.order.model

import com.stockary.common.repository.product.model.Units
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    var id: String? = null,
    val total: Double = 0.0,
    val discount: Double? = null,
    val status: String? = null,
    val user_id: String? = null,
    val items: List<OrderItem> = emptyList()
)

@Serializable
data class OrderItem(
    val quantity: Int = 0,
    val discount: Double = 0.0,
    val title: String? = null,
    val price: Double = 0.0,
    val product_id: String? = null,
    val units: Units? = null
)

@Serializable
data class OrderSummary(
    val title: String,
    @SerialName("category_name")
    val categoryName: String,
    @SerialName("unit_name")
    val unitName: String,
    @SerialName("total_unit")
    val totalUnit: Int
)