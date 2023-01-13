package com.stockary.common.repository.category.model

import com.stockary.common.repository.product.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val title: String,
    val icon: String,
    var products: List<Product> = emptyList()
)
