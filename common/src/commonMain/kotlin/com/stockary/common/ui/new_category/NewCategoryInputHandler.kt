package com.stockary.common.ui.new_category

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.CategoryRepository
import com.stockary.common.repository.category.model.Category
import kotlinx.coroutines.flow.map

class NewCategoryInputHandler(
    val categoryRepository: CategoryRepository
) : InputHandler<NewCategoryContract.Inputs, NewCategoryContract.Events, NewCategoryContract.State> {
    override suspend fun InputHandlerScope<NewCategoryContract.Inputs, NewCategoryContract.Events, NewCategoryContract.State>.handleInput(
        input: NewCategoryContract.Inputs
    ) = when (input) {
        is NewCategoryContract.Inputs.Initialize -> {

        }

        is NewCategoryContract.Inputs.GoBack -> {
            postEvent(NewCategoryContract.Events.NavigateUp)
        }

        is NewCategoryContract.Inputs.Save -> {
            updateState { it.copy(response = SupabaseResource.Loading) }
            observeFlows("SavingNewCategory") {
                listOf(categoryRepository.add(Category(title = input.title, description = input.description)).map {
                    NewCategoryContract.Inputs.UpdateSaveResponse(it)
                })
            }
        }

        is NewCategoryContract.Inputs.SaveAndContinue -> {
            updateState { it.copy(response = SupabaseResource.Loading) }
            observeFlows("SavingNewCategory") {
                listOf(categoryRepository.add(Category(title = input.title, description = input.description)).map {
                    NewCategoryContract.Inputs.UpdateSaveResponse(it, true)
                })
            }
        }

        is NewCategoryContract.Inputs.UpdateSaveResponse -> {
            if (input.response is SupabaseResource.Success && !input.isContinue) {
                postEvent(NewCategoryContract.Events.NavigateUp)
            }
            updateState { it.copy(response = input.response) }
        }
    }
}
