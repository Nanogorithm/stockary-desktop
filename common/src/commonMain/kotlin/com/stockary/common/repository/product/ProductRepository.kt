package com.stockary.common.repository.product

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.product.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    fun clearAllCaches()
    fun getDataList(refreshCache: Boolean = false): Flow<Cached<List<Product>>>

}
