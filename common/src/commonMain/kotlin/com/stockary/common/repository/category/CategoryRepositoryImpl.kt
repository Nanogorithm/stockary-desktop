package com.stockary.common.repository.category

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.withRepository
import com.stockary.common.repository.category.model.Category
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    coroutineScope: CoroutineScope,
    eventBus: EventBus,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BallastRepository<CategoryRepositoryContract.Inputs, CategoryRepositoryContract.State>(
    coroutineScope = coroutineScope, eventBus = eventBus, config = configBuilder.apply {
        inputHandler = CategoryRepositoryInputHandler(eventBus)
        initialState = CategoryRepositoryContract.State()
        name = "Category Repository"
    }.withRepository().build()
), CategoryRepository {
    override fun clearAllCaches() {
        trySend(CategoryRepositoryContract.Inputs.ClearCaches)
    }

    override fun getCategoryList(refreshCache: Boolean): Flow<Cached<List<Category>>> {
        trySend(CategoryRepositoryContract.Inputs.Initialize)
        trySend(CategoryRepositoryContract.Inputs.RefreshCategoryList(refreshCache))
        return observeStates().map { it.categories }
    }
}
