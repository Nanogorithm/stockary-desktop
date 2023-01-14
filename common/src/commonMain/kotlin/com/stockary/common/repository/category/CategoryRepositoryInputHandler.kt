package com.stockary.common.repository.category

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Count
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CategoryRepositoryInputHandler(
    private val eventBus: EventBus,
) : InputHandler<CategoryRepositoryContract.Inputs, Any, CategoryRepositoryContract.State>, KoinComponent {

    val supabaseClient: SupabaseClient by inject()

    override suspend fun InputHandlerScope<CategoryRepositoryContract.Inputs, Any, CategoryRepositoryContract.State>.handleInput(
        input: CategoryRepositoryContract.Inputs
    ) = when (input) {
        is CategoryRepositoryContract.Inputs.ClearCaches -> {
            updateState { CategoryRepositoryContract.State() }
        }

        is CategoryRepositoryContract.Inputs.Initialize -> {
            val previousState = getCurrentState()

            if (!previousState.initialized) {
                updateState { it.copy(initialized = true) }
                // start observing flows here
                logger.debug("initializing")
                observeFlows(
                    key = "Observe account changes",
                    eventBus.observeInputsFromBus(),
                )
            } else {
                logger.debug("already initialized")
                noOp()
            }
        }

        is CategoryRepositoryContract.Inputs.RefreshAllCaches -> {
            // then refresh all the caches in this repository
            val currentState = getCurrentState()
            if (currentState.categoriesInitialized) {
                postInput(CategoryRepositoryContract.Inputs.RefreshCategoryList(true))
            }

            Unit
        }

        is CategoryRepositoryContract.Inputs.CategoryListUpdated -> {
            updateState { it.copy(categories = input.dataList) }
        }

        is CategoryRepositoryContract.Inputs.RefreshCategoryList -> {
            updateState { it.copy(categoriesInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.categories },
                updateState = { CategoryRepositoryContract.Inputs.CategoryListUpdated(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["categories"].select("id,title,icon,products(*)")
                    println(result.body)
                    result.decodeList(json = Json {
                        ignoreUnknownKeys = true
                    })
                },
            )
        }
    }
}