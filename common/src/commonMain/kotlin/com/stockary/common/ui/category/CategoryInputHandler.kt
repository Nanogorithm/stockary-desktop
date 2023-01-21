package com.stockary.common.ui.category

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.stockary.common.repository.category.CategoryRepository
import kotlinx.coroutines.flow.map

class CategoryInputHandler(
    val categoryRepository: CategoryRepository
) : InputHandler<CategoryContract.Inputs, CategoryContract.Events, CategoryContract.State> {

    override suspend fun InputHandlerScope<CategoryContract.Inputs, CategoryContract.Events, CategoryContract.State>.handleInput(
        input: CategoryContract.Inputs
    ) = when (input) {
        is CategoryContract.Inputs.Initialize -> {
            postInput(CategoryContract.Inputs.FetchHotList(forceRefresh = true))
        }

        is CategoryContract.Inputs.GoBack -> {
            postEvent(CategoryContract.Events.NavigateUp)
        }

        is CategoryContract.Inputs.FetchHotList -> {
            observeFlows("FetchHotList") {
                listOf(categoryRepository.getCategoryList(refreshCache = input.forceRefresh)
                    .map { CategoryContract.Inputs.HotListUpdated(it) })
            }
        }

        is CategoryContract.Inputs.HotListUpdated -> {
            updateState { it.copy(categoryList = input.categoryList) }
        }

        CategoryContract.Inputs.AddNew -> {
            postEvent(CategoryContract.Events.AddNew)
        }

        is CategoryContract.Inputs.Delete -> {

        }

        is CategoryContract.Inputs.Edit -> {

        }
    }
}
