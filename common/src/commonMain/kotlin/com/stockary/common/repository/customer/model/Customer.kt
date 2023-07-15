package com.stockary.common.repository.customer.model

import com.helloanwar.common.ui.components.tableview.TableHeader
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
    @TableHeader("Email", 3) val email: String?,
    @TableHeader("address", 4) val address: String?,
    @TableHeader("Role", 5) val role: String?
)

@Serializable
data class Role(
    var id: String? = null, var title: String? = null, val slug: String? = ""
)


fun Profile.toCustomerTable(): CustomerTable {
    return CustomerTable(
        uid = this.uid,
        name = this.name,
        phone = this.phone,
        email = this.email,
        address = this.address,
        role = this.role
    )
}