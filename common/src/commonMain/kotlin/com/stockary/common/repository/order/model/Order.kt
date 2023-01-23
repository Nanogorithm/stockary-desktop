package com.stockary.common.repository.order.model

import com.stockary.common.repository.customer.model.Profile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: Int? = null,
    val total: Float = 0f,
    val discount: Float = 0f,
    val status: String,
    @SerialName("customer_id") val customerId: String,
    @SerialName("profiles") val profile: Profile? = null,
    @SerialName("order_items") val orderItems: List<OrderItem> = emptyList()
)

@Serializable
data class OrderItem(
    val id: Int? = null,
    val quantity: Int = 0,
    val discount: Float = 0f,
    @SerialName("product_name") val productName: String? = null,
    @SerialName("unit_price") val unitPrice: Float = 0f,
    @SerialName("order_id") val orderId: Int? = null,
    @SerialName("product_id") val productId: Int? = null,
)