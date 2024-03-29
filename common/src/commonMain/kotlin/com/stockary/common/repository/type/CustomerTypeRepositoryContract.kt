package com.stockary.common.repository.type

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.customer.model.Role

object CustomerTypeRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val customerTypeListInitialized: Boolean = false,
        val customerTypeList: Cached<List<Role>> = Cached.NotLoaded(),

        val saving: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val editing: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val deleting: SupabaseResource<Boolean> = SupabaseResource.Idle,
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshCustomerTypeList(val forceRefresh: Boolean) : Inputs()
        data class CustomerTypeListUpdated(val customerList: Cached<List<Role>>) : Inputs()
        data class Add(val title: String, val slug: String) : Inputs()
        data class Edit(val category: Category) : Inputs()
        data class Delete(val role: Role) : Inputs()
        data class UpdateAddResponse(val saving: SupabaseResource<Boolean>) : Inputs()
        data class UpdateDeleteResponse(val deleting: SupabaseResource<Boolean>) : Inputs()
    }
}
