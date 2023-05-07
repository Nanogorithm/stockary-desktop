package com.stockary.common.repository.product

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.withRepository
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.Product
import com.stockary.common.repository.product.model.UnitType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class ProductRepositoryImpl(
    coroutineScope: CoroutineScope,
    eventBus: EventBus,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BallastRepository<ProductRepositoryContract.Inputs, ProductRepositoryContract.State>(
    coroutineScope = coroutineScope, eventBus = eventBus, config = configBuilder.apply {
        inputHandler = ProductRepositoryInputHandler(eventBus)
        initialState = ProductRepositoryContract.State()
        name = "Product Repository"
    }.withRepository().build()
), ProductRepository {
    override fun clearAllCaches() {
        trySend(ProductRepositoryContract.Inputs.ClearCaches)
    }

    override fun getProductList(refreshCache: Boolean): Flow<Cached<List<Product>>> {
        trySend(ProductRepositoryContract.Inputs.Initialize)
        trySend(ProductRepositoryContract.Inputs.RefreshProductList(refreshCache))
        return observeStates().map { it.productList }
    }

    override fun getCustomerTypes(refreshCache: Boolean): Flow<Cached<List<Role>>> {
        trySend(ProductRepositoryContract.Inputs.RefreshCustomerTypes(refreshCache))
        return observeStates().map { it.customerTypes }
    }

    override fun getProductUnitTypes(refreshCache: Boolean): Flow<Cached<List<UnitType>>> {
        trySend(ProductRepositoryContract.Inputs.RefreshUnitTypes(refreshCache))
        return observeStates().map { it.unitTypes }
    }

    override fun uploadPhoto(file: File): Flow<SupabaseResource<String>> {
        trySend(ProductRepositoryContract.Inputs.UploadPhoto(file = file))
        return observeStates().map { it.photoUploadResponse }
    }

    override fun get(productId: String): Flow<SupabaseResource<Product>> {
        trySend(ProductRepositoryContract.Inputs.GetProduct(productId))
        return observeStates().map { it.product }
    }

    override fun add(
        product: Product, prices: List<Float>, types: List<Role>
    ): Flow<SupabaseResource<Boolean>> {
        trySend(ProductRepositoryContract.Inputs.Add(product = product, prices = prices, types = types))
        return observeStates().map { it.saving }
    }

    override fun edit(
        product: Product, updated: Product, prices: List<Float>, types: List<Role>
    ): Flow<SupabaseResource<Boolean>> {
        trySend(ProductRepositoryContract.Inputs.Edit(product, updated, prices, types))
        return observeStates().map { it.saving }
    }

    override fun delete(product: Product): Flow<SupabaseResource<Boolean>> {
        trySend(ProductRepositoryContract.Inputs.Delete(product = product))
        return observeStates().map { it.deleting }
    }

    override fun getPhotoUrl(url: String) {
        trySend(ProductRepositoryContract.Inputs.GetPhotoUrl(url))
    }
}
