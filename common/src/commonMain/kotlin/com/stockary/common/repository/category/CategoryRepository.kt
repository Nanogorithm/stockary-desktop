package com.stockary.common.repository.category

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.category.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun clearAllCaches()
    fun getCategoryList(refreshCache: Boolean = false): Flow<Cached<List<Category>>>
}
