package com.stockary.common.ui.category

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.model.Category
import io.appwrite.models.Document

object CategoryContract {
    data class State(
        val loading: Boolean = false,
        val categoryList: Cached<List<Document<Any>>> = Cached.NotLoaded(),
        val deleteResponse: SupabaseResource<Boolean> = SupabaseResource.Idle
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        object AddNew : Inputs()
        data class FetchHotList(val forceRefresh: Boolean) : Inputs()
        data class HotListUpdated(val categoryList: Cached<List<Document<Any>>>) : Inputs()
        data class Edit(val category: Category) : Inputs()
        data class Delete(val category: Category) : Inputs()
        data class UpdateDeleteResponse(val deleteResponse: SupabaseResource<Boolean>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
        object AddNew : Events()
    }
}
