package com.stockary.common.repository.category

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.category.model.Category

object CategoryRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val categoriesInitialized: Boolean = false,
        val categories: Cached<List<Category>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshCategoryList(val forceRefresh: Boolean) : Inputs()
        data class CategoryListUpdated(val dataList: Cached<List<Category>>) : Inputs()
    }
}
