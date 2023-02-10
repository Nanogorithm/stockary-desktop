package com.stockary.common.repository.customer

import com.copperleaf.ballast.BallastViewModelConfiguration
import com.copperleaf.ballast.build
import com.copperleaf.ballast.repository.BallastRepository
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.withRepository
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.product.model.Product
import io.github.jan.supabase.gotrue.providers.builtin.Email
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

    override fun getCustomerList(refreshCache: Boolean): Flow<Cached<List<Profile>>> {
        trySend(CustomerRepositoryContract.Inputs.Initialize)
        trySend(CustomerRepositoryContract.Inputs.RefreshCustomerList(refreshCache))
        return observeStates().map { it.dataList }
    }

    override fun get(customerId: Int): Flow<SupabaseResource<Product>> {
        TODO("Not yet implemented")
    }

    override fun add(email: String, password: String, roleId: Int): Flow<SupabaseResource<Email.Result>> {
        trySend(CustomerRepositoryContract.Inputs.Add(email = email, password = password))
        return observeStates().map { it.saving }
    }

    override fun edit(customer: Profile, updated: Profile): Flow<SupabaseResource<Boolean>> {
        TODO("Not yet implemented")
    }

    override fun delete(customer: Profile): Flow<SupabaseResource<Boolean>> {
        TODO("Not yet implemented")
    }
}
