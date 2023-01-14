package com.stockary.common.repository.order

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.stockary.common.repository.order.model.Order
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class OrderRepositoryInputHandler(
    private val eventBus: EventBus,
) : InputHandler<OrderRepositoryContract.Inputs, Any, OrderRepositoryContract.State>, KoinComponent {

    val supabaseClient: SupabaseClient by inject()

    override suspend fun InputHandlerScope<OrderRepositoryContract.Inputs, Any, OrderRepositoryContract.State>.handleInput(
        input: OrderRepositoryContract.Inputs
    ) = when (input) {
        is OrderRepositoryContract.Inputs.ClearCaches -> {
            updateState { OrderRepositoryContract.State() }
        }

        is OrderRepositoryContract.Inputs.Initialize -> {
            val previousState = getCurrentState()

            if (!previousState.initialized) {
                updateState { it.copy(initialized = true) }
                // start observing flows here
                logger.debug("initializing")
                observeFlows(
                    key = "Observe account changes",
                    eventBus.observeInputsFromBus<OrderRepositoryContract.Inputs>(),
                )
            } else {
                logger.debug("already initialized")
                noOp()
            }
        }

        is OrderRepositoryContract.Inputs.RefreshAllCaches -> {
            // then refresh all the caches in this repository
            val currentState = getCurrentState()
            if (currentState.dataListInitialized) {
                postInput(OrderRepositoryContract.Inputs.RefreshDataList(true))
            }

            Unit
        }

        is OrderRepositoryContract.Inputs.DataListUpdated -> {
            updateState { it.copy(dataList = input.dataList) }
        }

        is OrderRepositoryContract.Inputs.RefreshDataList -> {
            updateState { it.copy(dataListInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.dataList },
                updateState = { OrderRepositoryContract.Inputs.DataListUpdated(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["orders"].select("*")
                    println(result.body)
                    result.decodeList<Order>(json = Json { ignoreUnknownKeys = true }).let {
                        println(it)
                        it
                    }
                },
            )
        }
    }
}