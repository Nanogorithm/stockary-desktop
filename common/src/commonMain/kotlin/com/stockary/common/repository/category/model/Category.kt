package com.stockary.common.repository.category.model

import com.stockary.common.repository.product.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    var id: Int? = null,
    val title: String,
    var icon: String? = null,
    var description: String? = null,
    var products: List<Product> = emptyList()
)
