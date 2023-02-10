package com.stockary.common.repository.customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.stockary.common.SupabaseResource
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
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
                postInput(CustomerRepositoryContract.Inputs.RefreshCustomerList(true))
            }

            Unit
        }

        is CustomerRepositoryContract.Inputs.CustomerListUpdated -> {
            updateState { it.copy(dataList = input.customerList) }
        }

        is CustomerRepositoryContract.Inputs.RefreshCustomerList -> {
            updateState { it.copy(dataListInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.dataList },
                updateState = { CustomerRepositoryContract.Inputs.CustomerListUpdated(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["profiles"].select("*,customer_roles(*)")
                    println(result.body)
                    result.decodeList(json = Json { ignoreUnknownKeys = true })
                },
            )
        }

        is CustomerRepositoryContract.Inputs.Add -> {
            sideJob("AddCustomer") {
                val supabaseResponse: SupabaseResource<Email.Result> = try {
                    val response = supabaseClient.gotrue.signUpWith(Email) {
                        email = input.email
                        password = input.password
                    }
                    SupabaseResource.Success(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                    SupabaseResource.Error(e)
                }
                //update state
                postInput(CustomerRepositoryContract.Inputs.UpdateSignupResponse(supabaseResponse))
            }
        }

        is CustomerRepositoryContract.Inputs.Delete -> {

        }

        is CustomerRepositoryContract.Inputs.Edit -> {

        }

        is CustomerRepositoryContract.Inputs.UpdateSignupResponse -> {
            updateState { it.copy(saving = input.saving) }
        }
    }
}
