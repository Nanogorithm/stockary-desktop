package com.stockary.common.repository.type

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CustomerTypeRepositoryImpl(
    coroutineScope: CoroutineScope,
    eventBus: EventBus,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BallastRepository<CustomerTypeRepositoryContract.Inputs, CustomerTypeRepositoryContract.State>(
    coroutineScope = coroutineScope, eventBus = eventBus, config = configBuilder.build()
), CustomerTypeRepository {
    override fun clearAllCaches() {
        trySend(CustomerTypeRepositoryContract.Inputs.ClearCaches)
    }

    override fun getCustomerTypeList(refreshCache: Boolean): Flow<Cached<List<Role>>> {
        trySend(CustomerTypeRepositoryContract.Inputs.Initialize)
        trySend(CustomerTypeRepositoryContract.Inputs.RefreshCustomerTypeList(refreshCache))
        return observeStates().map { it.customerTypeList }
    }

    override fun add(title: String, slug: String): Flow<SupabaseResource<Boolean>> {
        trySend(CustomerTypeRepositoryContract.Inputs.Add(title, slug))
        return observeStates().map { it.saving }
    }

    override fun get(typeId: String): Flow<SupabaseResource<Role>> {
        TODO()
    }

    override fun edit(type: Role, updated: Role): Flow<SupabaseResource<Boolean>> {
        return observeStates().map { it.saving }
    }

    override fun delete(role: Role): Flow<SupabaseResource<Boolean>> {
        trySend(CustomerTypeRepositoryContract.Inputs.Delete(role))
        return observeStates().map { it.deleting }
    }
}
