package com.stockary.common.repository.customer.model

import com.helloanwar.common.ui.components.tableview.TableHeader
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

data class CustomerTable(
    @TableHeader("ID", 0) val uid: String?,
    @TableHeader("Name", 1) val name: String?,
    @TableHeader("Phone", 2) val phone: String?,
    @TableHeader("address", 3) val address: String?,
    @TableHeader("Role", 4) val role: String?
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


fun Profile.toCustomerTable(): CustomerTable {
    return CustomerTable(
        uid = this.uid,
        name = this.name,
        phone = this.phone,
        address = this.address,
        role = this.role
    )
}