package com.stockary.common.repository.category

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.model.Category

object CategoryRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val categoriesInitialized: Boolean = false,
        val categories: Cached<List<Category>> = Cached.NotLoaded(),
        val saving: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val editing: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val deleting: SupabaseResource<Boolean> = SupabaseResource.Idle,
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshCategoryList(val forceRefresh: Boolean) : Inputs()
        data class CategoryListUpdated(val dataList: Cached<List<Category>>) : Inputs()

        data class Add(val category: Category) : Inputs()
        data class Edit(val category: Category, val updated: Category) : Inputs()
        data class Delete(val category: Category) : Inputs()
    }
}
