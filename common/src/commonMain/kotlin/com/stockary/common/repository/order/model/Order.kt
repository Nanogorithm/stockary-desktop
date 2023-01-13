package com.stockary.common.repository.order.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int,
    val total: Float,
    val discount: Float,
    val status: String,
    @SerialName("user_id") val userId: String,
    val orderItem: OrderItem
)

@Serializable
data class OrderItem(
    val id: Int,
    @SerialName("product_name") val productName: String,
    @SerialName("unit_price") val unitPrice: Float,
    val quantity: Int,
    val discount: Float
)