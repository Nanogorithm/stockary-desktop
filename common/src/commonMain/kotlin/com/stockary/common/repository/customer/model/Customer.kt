package com.stockary.common.repository.customer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: Int,
    @SerialName("user_id") val userId: String,
    val email: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val avatar: String?,
    @SerialName("role_id") val roleId: Int?,
    @SerialName("customer_roles") val role: Role?
)

@Serializable
data class Role(
    val id: Int, val name: String
)
