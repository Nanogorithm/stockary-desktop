package com.stockary.common.repository.customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.InviteInput
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
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
                val supabaseResponse: SupabaseResource<Boolean> = try {
                    val data = InviteInput(
                        email = input.email, name = input.name, address = input.address, roleId = input.roleId
                    )
                    supabaseClient.gotrue.importAuthToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im5md3dhanhxZWlscWRrdndmb2p6Iiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTY3MzUxNDc1MSwiZXhwIjoxOTg5MDkwNzUxfQ.VqIPOoipJOmqylpBWMvjeHpbVCZAPiipTJB2DpAa1XE")
                    supabaseClient.gotrue.admin.inviteUserByEmail(
                        email = input.email,
                        data = Json.encodeToJsonElement(data).jsonObject,
                        redirectTo = "https://stockary.co/welcome"
                    )
                    SupabaseResource.Success(true)
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

        is CustomerRepositoryContract.Inputs.RefreshCustomerTypes -> {
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.customerTypes },
                updateState = { CustomerRepositoryContract.Inputs.UpdateCustomerTypes(it) },
                doFetch = {
                    val result = supabaseClient.postgrest["customer_roles"].select("*")
                    println("Customer types => ${result.body}")
                    result.decodeList(json = Json {
                        ignoreUnknownKeys = true
                    })
                },
            )
        }

        is CustomerRepositoryContract.Inputs.UpdateCustomerTypes -> {
            updateState { it.copy(customerTypes = input.dataList) }
        }
    }
}
