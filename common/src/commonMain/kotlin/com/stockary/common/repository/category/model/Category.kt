package com.stockary.common.repository.category.model

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    var id: String? = null,
    var title: String = "",
    var noteApplicable: Boolean = false,
    var icon: String? = null,
    var description: String? = null,
    var sort: Int? = null,
)
