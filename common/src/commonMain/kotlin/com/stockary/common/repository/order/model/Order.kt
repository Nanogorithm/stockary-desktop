package com.stockary.common.repository.order.model

import com.helloanwar.common.ui.components.tableview.TableHeader
import com.stockary.common.repository.product.model.Units
import com.stockary.common.toCurrencyFormat
import jdk.jfr.Enabled
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    var id: String? = null,
    val total: Double = 0.0,
    val discount: Double? = null,
    val status: String? = null,
    val user_id: String? = null,
    val customer_name: String? = null,
    var createdAt: Instant? = null,
    val items: List<OrderItem> = emptyList()
)

data class OrderTable(
    var userId: String?,
    @TableHeader("ID", 0) val id: String? = null,
    @TableHeader("Customer", 1) val customerName: String?,
    @TableHeader("Quantity", 2) val quantity: Float,
    @TableHeader("Total", 3) val total: String?,
    @TableHeader("Time", 4) val createdAt: String?,
    @TableHeader("Status", 5) val status: String?
)

fun Order.toOrderTable(): OrderTable {
    return OrderTable(
        id = this.id,
        userId = this.user_id,
        customerName = this.customer_name,
        status = this.status,
        createdAt = this.createdAt?.toString(),
        total = this.total.toCurrencyFormat(),
        quantity = this.items.sumOf {
            it.quantity.toDouble()
        }.toFloat()
    )
}

fun Order.toOrderSummaryItem(): List<OrderSummaryItem> {
    return this.items.map {
        OrderSummaryItem(
            userId = this.user_id,
            customerName = this.customer_name,
            productName = it.title,
            quantity = it.quantity,
            productId = it.product_id,
            category = it.category,
            units = it.units,
            notes = it.note,
            isNoteEnabled = it.note != null
        )
    }
}


@Serializable
data class OrderItem(
    val quantity: Float = 0f,
    val price: Double = 0.0,
    val discount: Double = 0.0,
    val title: String? = null,
    val product_id: String? = null,
    val category: String? = null,
    val units: Units? = null,
    val note: Note? = null
)

@Serializable
data class Note(
    val text: String? = null,
    val photo: String? = null
)

data class OrderSummary(
    val userId: String?,
    val customerName: String?,
    val items: List<OrderSummaryItem>
)

data class OrderSummaryItem(
    val userId: String?,
    val customerName: String?,

    val quantity: Float = 0f,
    val productName: String? = null,
    val productId: String? = null,
    val category: String? = null,
    val isNoteEnabled: Boolean = false,
    val units: Units? = null,
    val notes: Note? = null
)

data class OrderSummaryTable(
    val userId: String?,
    val customerName: String?,
    @TableHeader("Product Name", 1)
    val productName: String?,
    @TableHeader("Category", 0)
    val categoryName: String?,
    @TableHeader("Amount", 2)
    val totalUnit: Float,
    @TableHeader("Unit", 3)
    val unitName: String,
    val note: Note? = null
)