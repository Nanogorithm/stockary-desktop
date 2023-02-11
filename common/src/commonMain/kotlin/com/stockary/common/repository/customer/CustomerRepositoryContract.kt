package com.stockary.common.repository.customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Profile
import io.github.jan.supabase.gotrue.providers.builtin.Email

object CustomerRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val dataList: Cached<List<Profile>> = Cached.NotLoaded(),
        val saving: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val editing: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val deleting: SupabaseResource<Boolean> = SupabaseResource.Idle,
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshCustomerList(val forceRefresh: Boolean) : Inputs()
        data class CustomerListUpdated(val customerList: Cached<List<Profile>>) : Inputs()

        data class Add(val email: String, val name: String, val address: String, val roleId: Int) : Inputs()
        data class UpdateSignupResponse(val saving: SupabaseResource<Boolean>) : Inputs()
        data class Edit(val category: Profile, val updated: Profile) : Inputs()
        data class Delete(val category: Profile) : Inputs()
    }
}
