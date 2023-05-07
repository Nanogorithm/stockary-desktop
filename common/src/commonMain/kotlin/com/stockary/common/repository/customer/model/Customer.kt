package com.stockary.common.repository.customer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    var uid: String? = null,
    var email: String? = null,
    var name: String = "",
    var company: String? = null,
    var address: String? = null,
    var phone: String? = null,
    var avatar: String? = null,
    var role: String? = null
)

@Serializable
data class Role(
    var id: String? = null, var title: String? = null, val slug: String? = ""
)

@Serializable
data class InviteInput(
    val email: String,
    @SerialName("full_name") val name: String,
    val address: String,
    @SerialName("role_id") val roleId: Int,
    @SerialName("avatar_url") val avatar: String = ""
)
