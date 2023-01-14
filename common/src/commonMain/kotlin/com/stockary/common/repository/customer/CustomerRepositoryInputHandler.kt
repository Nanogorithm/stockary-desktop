package com.stockary.common.repository.customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class CustomerRepositoryInputHandler(
    private val eventBus: EventBus,
) : InputHandler<CustomerRepositoryContract.Inputs, Any, CustomerRepositoryContract.State>, KoinComponent {

    val supabaseClient: SupabaseClient by inject()

    override suspend fun InputHandlerScope<CustomerRepositoryContract.Inputs, Any, CustomerRepositoryContract.State>.handleInput(
        input: CustomerRepositoryContract.Inputs
    ) = when (input) {
        is CustomerRepositoryContract.Inputs.ClearCaches -> {
            updateState { CustomerRepositoryContract.State() }
        }

        is CustomerRepositoryContract.Inputs.Initialize -> {
            val previousState = getCurrentState()

            if (!previousState.initialized) {
                updateState { it.copy(initialized = true) }
                // start observing flows here
                logger.debug("initializing")
                observeFlows(
                    key = "Observe account changes",
                    eventBus.observeInputsFromBus<CustomerRepositoryContract.Inputs>(),
                )
            } else {
                logger.debug("already initialized")
                noOp()
            }
        }

        is CustomerRepositoryContract.Inputs.RefreshAllCaches -> {
            // then refresh all the caches in this repository
            val currentState = getCurrentState()
            if (currentState.dataListInitialized) {
                postInput(CustomerRepositoryContract.Inputs.RefreshDataList(true))
            }

            Unit
        }

        is CustomerRepositoryContract.Inputs.DataListUpdated -> {
            updateState { it.copy(dataList = input.dataList) }
        }

        is CustomerRepositoryContract.Inputs.RefreshDataList -> {
            updateState { it.copy(dataListInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.dataList },
                updateState = { CustomerRepositoryContract.Inputs.DataListUpdated(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["profiles"].select("*,customer_roles(*)")
                    println(result.body)
                    result.decodeList(json = Json { ignoreUnknownKeys = true })
                },
            )
        }
    }
}
