package com.stockary.common.repository.product

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.withRepository
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.product.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepositoryImpl(
    coroutineScope: CoroutineScope,
    eventBus: EventBus,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BallastRepository<
        ProductRepositoryContract.Inputs,
        ProductRepositoryContract.State>(
    coroutineScope = coroutineScope, eventBus = eventBus, config = configBuilder
        .apply {
            inputHandler = ProductRepositoryInputHandler(eventBus)
            initialState = ProductRepositoryContract.State()
            name = "Product Repository"
        }.withRepository().build()
), ProductRepository {
    override fun clearAllCaches() {
        trySend(ProductRepositoryContract.Inputs.ClearCaches)
    }

    override fun getDataList(refreshCache: Boolean): Flow<Cached<List<Product>>> {
        trySend(ProductRepositoryContract.Inputs.Initialize)
        trySend(ProductRepositoryContract.Inputs.RefreshDataList(refreshCache))
        return observeStates()
            .map { it.dataList }
    }

    override fun add(product: Product): SupabaseResource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun edit(product: Product, updated: Product): SupabaseResource<Boolean> {
        TODO("Not yet implemented")
    }

    override fun delete(product: Product): SupabaseResource<Boolean> {
        TODO("Not yet implemented")
    }
}
