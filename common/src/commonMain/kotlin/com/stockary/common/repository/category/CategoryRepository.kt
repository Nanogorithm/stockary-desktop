package com.stockary.common.repository.category

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.category.model.Category
import io.appwrite.models.Document
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun clearAllCaches()
    fun getCategoryList(refreshCache: Boolean = false): Flow<Cached<List<Category>>>
    fun getCategoryListt(refreshCache: Boolean = false): Flow<Cached<List<Document<Any>>>>
    fun add(category: Category): Flow<SupabaseResource<Boolean>>
    fun edit(category: Category, updated: Category): Flow<SupabaseResource<Boolean>>
    fun delete(category: Category): Flow<SupabaseResource<Boolean>>
}
