package com.stockary.common.repository.type

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.google.firebase.cloud.FirestoreClient
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.Role

class CustomerTypeRepositoryInputHandler(
    private val eventBus: EventBus,
) : InputHandler<CustomerTypeRepositoryContract.Inputs, Any, CustomerTypeRepositoryContract.State> {
    override suspend fun InputHandlerScope<CustomerTypeRepositoryContract.Inputs, Any, CustomerTypeRepositoryContract.State>.handleInput(
        input: CustomerTypeRepositoryContract.Inputs
    ) = when (input) {
        is CustomerTypeRepositoryContract.Inputs.ClearCaches -> {
            updateState { CustomerTypeRepositoryContract.State() }
        }

        is CustomerTypeRepositoryContract.Inputs.Initialize -> {
            val previousState = getCurrentState()

            if (!previousState.initialized) {
                updateState { it.copy(initialized = true) }
                // start observing flows here
                logger.debug("initializing")
                observeFlows(
                    key = "Observe account changes",
                    eventBus.observeInputsFromBus<CustomerTypeRepositoryContract.Inputs>(),
                )
            } else {
                logger.debug("already initialized")
                noOp()
            }
        }

        is CustomerTypeRepositoryContract.Inputs.RefreshAllCaches -> {
            // then refresh all the caches in this repository
            val currentState = getCurrentState()
            if (currentState.customerTypeListInitialized) {
                postInput(CustomerTypeRepositoryContract.Inputs.RefreshCustomerTypeList(true))
            }

            Unit
        }

        is CustomerTypeRepositoryContract.Inputs.CustomerTypeListUpdated -> {
            updateState { it.copy(customerTypeList = input.customerList) }
        }

        is CustomerTypeRepositoryContract.Inputs.RefreshCustomerTypeList -> {
            updateState { it.copy(customerTypeListInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.customerTypeList },
                updateState = { CustomerTypeRepositoryContract.Inputs.CustomerTypeListUpdated(it) },
                doFetch = {
                    val firestore = FirestoreClient.getFirestore()
                    val future = firestore.collection("types").get()
                    val data = future.get()

                    data.documents.mapNotNull { snap ->
                        try {
                            val role = snap.toObject(Role::class.java)
                            role?.apply {
                                id = snap.id
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is CustomerTypeRepositoryContract.Inputs.Add -> {
            val response = try {
                val firestore = FirestoreClient.getFirestore()
                val data = firestore.collection("types").add(
                    mapOf(
                        "title" to input.title, "slug" to input.slug
                    )
                )
                SupabaseResource.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                SupabaseResource.Error(e)
            }
            postInput(CustomerTypeRepositoryContract.Inputs.UpdateAddResponse(response))
        }

        is CustomerTypeRepositoryContract.Inputs.UpdateAddResponse -> {
            updateState { it.copy(saving = input.saving) }
        }

        is CustomerTypeRepositoryContract.Inputs.Delete -> {
            val response = try {
                val firestore = FirestoreClient.getFirestore()
                val data = firestore.collection("types").document(input.role.id!!).delete()
                SupabaseResource.Success(true)
            } catch (e: Exception) {
                e.printStackTrace()
                SupabaseResource.Error(e)
            }

            postInput(CustomerTypeRepositoryContract.Inputs.UpdateDeleteResponse(response))
        }

        is CustomerTypeRepositoryContract.Inputs.Edit -> {

        }

        is CustomerTypeRepositoryContract.Inputs.UpdateDeleteResponse -> {
            if (input.deleting is SupabaseResource.Success) {
                postInput(CustomerTypeRepositoryContract.Inputs.RefreshCustomerTypeList(true))
            }
            updateState { it.copy(deleting = input.deleting) }
        }
    }
}
