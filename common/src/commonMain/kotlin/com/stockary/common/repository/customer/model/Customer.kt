package com.stockary.common.repository.customer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String,
    val email: String,
    @SerialName("name") val name: String,
    val company: String? = null,
    val address: String? = null,
    val avatar: String? = null,
    @SerialName("role_id") val roleId: Int?,
    @SerialName("customer_roles") val role: Role? = null
)

@Serializable
data class Role(
    val id: Int, val name: String
)

@Serializable
data class InviteInput(
    val email: String,
    @SerialName("full_name") val name: String,
    val address: String,
    @SerialName("role_id") val roleId: Int,
    @SerialName("avatar_url")
    val avatar: String = ""
)
