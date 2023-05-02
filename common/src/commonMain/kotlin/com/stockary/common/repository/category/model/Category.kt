package com.stockary.common.repository.category.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    var id: String? = null,
    var title: String = "",
    var icon: String? = null,
    var description: String? = null
)
