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
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.customer.model.Role
import io.github.jan.supabase.SupabaseClient
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
            try {
                val firestore = FirestoreClient.getFirestore()
                //update role claims
                val firebaseAuth = FirebaseAuth.getInstance()

                val data = firestore.collection("users").document(input.customer.uid!!).set(
                    mapOf(
                        "name" to input.updated.name,
                        "address" to input.updated.address,
                        "role" to input.updated.role,
                        "phone" to input.updated.phone,
                        "email" to input.updated.email
                    ), SetOptions.merge()
                )

                //update data
                if (input.updated.role != input.customer.role) {
                    val claims = mapOf(
                        "role" to input.updated.role
                    )

                    firebaseAuth.setCustomUserClaims(input.customer.uid!!, claims)
                }

                //update auth user data
                if (input.customer.phone != input.updated.phone || input.customer.email != input.updated.email || input.customer.name != input.updated.name) {
                    val request: UserRecord.UpdateRequest = UserRecord.UpdateRequest(input.customer.uid).apply {
                        if (input.customer.phone != input.updated.phone) {
                            if (!input.updated.phone.isNullOrBlank()) {
                                setPhoneNumber(input.updated.phone)
                            }
                        }

                        if (input.customer.name != input.updated.name) {
                            if (input.updated.name?.isNotBlank() == true) {
                                setDisplayName(input.updated.name)
                            }
                        }

                        if (input.customer.email != input.updated.email) {
                            if (!input.updated.email.isNullOrBlank()) {
                                setEmail(input.updated.email)
                            }
                        }
                    }

                    val response = firebaseAuth.updateUser(request)
                }

                postInput(CustomerRepositoryContract.Inputs.UpdateSignupResponse(SupabaseResource.Success(true)))
            } catch (e: Exception) {
                e.printStackTrace()
                postInput(CustomerRepositoryContract.Inputs.UpdateSignupResponse(SupabaseResource.Error(e)))
            }
            Unit
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

        is CustomerRepositoryContract.Inputs.GetCustomer -> {
            try {
                val firestore = FirestoreClient.getFirestore()
                val data = firestore.collection("users").document(input.customerId).get().get()
                val customer = data.toObject(Profile::class.java)

                val firebaseAuth = FirebaseAuth.getInstance()
                val userRole = firebaseAuth.getUser(input.customerId).customClaims["role"] as String?

                println("actual role => $userRole")

                customer?.apply {
                    role = userRole
                }

                updateState { it.copy(customer = SupabaseResource.Success(customer!!)) }
            } catch (e: Exception) {
                e.printStackTrace()
                updateState { it.copy(customer = SupabaseResource.Error(e)) }
            }
        }

        is CustomerRepositoryContract.Inputs.UpdateCustomer -> {
            updateState { it.copy(customer = input.customer) }
        }
    }
}
