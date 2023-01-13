package com.stockary.common.repository.product.model

import com.stockary.common.repository.category.model.Category
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int,
    val title: String,
    val price: Float,
    val stock: Int,
    val sort: Int,
    @SerialName("category_id") val categoryId: Int,
    @SerialName("categories") var category: Category? = null
)