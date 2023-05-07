package com.stockary.common.repository.customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.customer.model.Role

object CustomerRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val dataList: Cached<List<Profile>> = Cached.NotLoaded(),
        val customerTypes: Cached<List<Role>> = Cached.NotLoaded(),

        val saving: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val customer: SupabaseResource<Profile> = SupabaseResource.Idle,
        val editing: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val deleting: SupabaseResource<Boolean> = SupabaseResource.Idle,
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshCustomerList(val forceRefresh: Boolean) : Inputs()
        data class CustomerListUpdated(val customerList: Cached<List<Profile>>) : Inputs()

        data class RefreshCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val dataList: Cached<List<Role>>) : Inputs()

        data class Add(val email: String, val name: String, val address: String, val role: String, val phone: String) :
            Inputs()

        data class UpdateSignupResponse(val saving: SupabaseResource<Boolean>) : Inputs()
        data class Edit(val customer: Profile, val updated: Profile) : Inputs()
        data class Delete(val category: Profile) : Inputs()
        data class GetCustomer(val customerId: String) : Inputs()
        data class UpdateCustomer(val customer: SupabaseResource<Profile>) : Inputs()
    }
}
