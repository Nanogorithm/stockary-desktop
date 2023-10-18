package com.stockary.common.repository.category

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.google.cloud.firestore.SetOptions
import com.google.firebase.cloud.FirestoreClient
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.model.Category
import io.github.jan.supabase.SupabaseClient
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

                    val firestore = FirestoreClient.getFirestore()

                    val future = firestore.collection("categories").get()
                    val data = future.get()

                    data.documents.mapNotNull { docSnapshot ->
                        try {
                            val cat = docSnapshot.toObject(Category::class.java)
                            cat.apply {
                                id = docSnapshot.id
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is CategoryRepositoryContract.Inputs.Add -> {
            try {
                val firestore = FirestoreClient.getFirestore()
                val category = firestore.collection("categories").add(
                    mapOf(
                        "title" to input.category.title,
                        "noteApplicable" to input.category.noteApplicable
                    )
                )

                updateState { it.copy(saving = SupabaseResource.Success(true)) }
                postInput(CategoryRepositoryContract.Inputs.RefreshCategoryList(true))
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(saving = SupabaseResource.Error(e)) }
            }
        }

        is CategoryRepositoryContract.Inputs.Delete -> {
            try {
                val firestore = FirestoreClient.getFirestore()
                val category = firestore.collection("categories").document(input.category.id!!).delete()
                postInput(CategoryRepositoryContract.Inputs.RefreshCategoryList(true))
                updateState { it.copy(deleting = SupabaseResource.Success(true)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(deleting = SupabaseResource.Error(e)) }
            }
        }

        is CategoryRepositoryContract.Inputs.Edit -> {
            try {
                val firestore = FirestoreClient.getFirestore()
                val category = firestore.collection("categories").document(input.category.id!!).set(
                    mutableMapOf<String?, Any?>().apply {
                        if (input.category.title != input.updated.title) {
                            "title" to input.updated.title
                        }
                    },
                    SetOptions.merge()
                )
                updateState { it.copy(editing = SupabaseResource.Success(true)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(editing = SupabaseResource.Error(e)) }
            }
        }
    }
}