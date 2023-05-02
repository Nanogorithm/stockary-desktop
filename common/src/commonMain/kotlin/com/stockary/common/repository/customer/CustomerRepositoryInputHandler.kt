package com.stockary.common.repository.customer

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.google.cloud.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserRecord
import com.google.firebase.cloud.FirestoreClient
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.customer.model.InviteInput
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.customer.model.Role
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.gotrue.gotrue
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
                    val firestore = FirestoreClient.getFirestore()

                    val future = firestore.collection("users").get()
                    val data = future.get()

                    data.documents.mapNotNull { docSnap ->
                        try {
                            docSnap.toObject(Profile::class.java)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is CustomerRepositoryContract.Inputs.Add -> {
            sideJob("AddCustomer") {
                val supabaseResponse: SupabaseResource<Boolean> = try {

                    val firebaseAuth = FirebaseAuth.getInstance()

                    val request = UserRecord.CreateRequest().apply {
                        if (input.email.isNotBlank()) {
                            setEmail(input.email)
                            setEmailVerified(true)
                        }
                        setPhoneNumber(input.phone)
                    }.setDisplayName(input.name).setDisabled(false)

                    val user = firebaseAuth.createUser(request)

                    val claims = mapOf(
                        "role" to input.role
                    )

                    firebaseAuth.setCustomUserClaims(user.uid, claims)

                    val firestore = FirestoreClient.getFirestore()

                    val response = firestore.collection("users").document(user.uid).set(
                        mapOf(
                            "name" to input.name,
                            "role" to input.role,
                            "address" to input.address,
                            "phone" to input.phone,
                            "uid" to user.uid
                        ), SetOptions.merge()
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
                    val firestore = FirestoreClient.getFirestore()
                    val future = firestore.collection("types").get()
                    val data = future.get()

                    data.documents.mapNotNull { docSnap ->
                        try {
                            val role = docSnap.toObject(Role::class.java)
                            role?.apply {
                                id = docSnap.id
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is CustomerRepositoryContract.Inputs.UpdateCustomerTypes -> {
            updateState { it.copy(customerTypes = input.dataList) }
        }
    }
}
