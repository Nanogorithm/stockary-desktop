package com.stockary.common.repository.customer

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.withRepository
import com.stockary.common.repository.customer.model.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CustomerRepositoryImpl(
    coroutineScope: CoroutineScope,
    eventBus: EventBus,
    configBuilder: BallastViewModelConfiguration.Builder,
) : BallastRepository<CustomerRepositoryContract.Inputs, CustomerRepositoryContract.State>(
    coroutineScope = coroutineScope, eventBus = eventBus, config = configBuilder.apply {
        inputHandler = CustomerRepositoryInputHandler(eventBus)
        initialState = CustomerRepositoryContract.State()
        name = "Customer Repository"
    }.withRepository().build()
), CustomerRepository {
    override fun clearAllCaches() {
        trySend(CustomerRepositoryContract.Inputs.ClearCaches)
    }

    override fun getDataList(refreshCache: Boolean): Flow<Cached<List<Profile>>> {
        trySend(CustomerRepositoryContract.Inputs.Initialize)
        trySend(CustomerRepositoryContract.Inputs.RefreshDataList(refreshCache))
        return observeStates().map { it.dataList }
    }
}
