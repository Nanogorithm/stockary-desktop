package com.stockary.common.repository.product.model

import com.helloanwar.common.ui.components.tableview.ColumnType
import com.helloanwar.common.ui.components.tableview.TableHeader
import com.stockary.common.removeEmptyFraction
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

    val media: Media? = null,
    val code: String? = null,
    val prices: Map<String, Double>? = null,
    val units: Units? = null,

    var category: String? = null,
    val productCustomerRole: List<ProductCustomerRole> = emptyList()
)


data class ProductTable(
    val id: String?,
    @TableHeader("Product Code", 0) val code: String? = null,
    @TableHeader("Photo", 1) val image: ColumnType,
    @TableHeader("Title", 2) val title: String,
    @TableHeader("Prices", 3) val prices: String? = null,
    @TableHeader("Unit", 4) val unitAmount: String,
    @TableHeader("Category", 5) val category: String?
)

fun Product.toProductTable(): ProductTable {
    return ProductTable(
        id = this.id,
        code = this.code,
        title = this.title,
        image = ColumnType.Image(src = this.media?.url),
        unitAmount = "${this.units?.amount?.removeEmptyFraction() ?: ""} ${this.units?.type ?: ""}",
        category = this.category,
        prices = this.prices?.mapNotNull {
            if (it.value > 0) {
                "${it.key} : ${it.value.removeEmptyFraction()}"
            } else null
        }?.joinToString("\n")
    )
}

@Serializable
data class Price(
    val dealer: Double = 0.0, val customer: Double = 0.0
)

@Serializable
data class Units(
    val amount: Float = 0f, val type: String? = null
)

@Serializable
data class Media(
    val path: String? = null, val url: String? = null
)

enum class MediaType {
    IMAGE, VIDEO, PDF, LINK
}

@Serializable
data class ProductCustomerRole(
    val id: Int? = null, val product_id: Int, val customer_role_id: Int, val price: Float
)

@Serializable
data class UnitType(
    var id: String? = null, val name: String = ""
)