package com.stockary.common.ui.category

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.category.model.Category

object CategoryContract {
    data class State(
        val loading: Boolean = false,
        val categoryList: Cached<List<Category>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        data class FetchHotList(val forceRefresh: Boolean) : Inputs()
        data class HotListUpdated(val categoryList: Cached<List<Category>>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
