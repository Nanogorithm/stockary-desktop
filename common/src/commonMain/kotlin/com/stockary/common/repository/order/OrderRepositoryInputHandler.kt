package com.stockary.common.repository.order

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.copperleaf.ballast.repository.bus.EventBus
import com.copperleaf.ballast.repository.bus.observeInputsFromBus
import com.copperleaf.ballast.repository.cache.fetchWithCache
import com.google.cloud.firestore.Query
import com.stockary.common.endOfDay
import com.stockary.common.repository.order.model.Order
import kotlinx.datetime.Instant
import org.koin.core.component.KoinComponent
import java.util.*


class OrderRepositoryInputHandler(
    private val eventBus: EventBus,
) : InputHandler<OrderRepositoryContract.Inputs, Any, OrderRepositoryContract.State>, KoinComponent {
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
                postInput(OrderRepositoryContract.Inputs.RefreshOrders(true))
            }

            Unit
        }

        is OrderRepositoryContract.Inputs.UpdateOrders -> {
            updateState { it.copy(orderList = input.dataList) }
        }

        is OrderRepositoryContract.Inputs.RefreshOrders -> {
            updateState { it.copy(dataListInitialized = true) }
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.orderList },
                updateState = { OrderRepositoryContract.Inputs.UpdateOrders(it) },
                doFetch = {
                    val firestore = com.google.firebase.cloud.FirestoreClient.getFirestore()

                    val future = if (input.isSingleDay) firestore.collection("orders")
                        .whereGreaterThanOrEqualTo("created_at", input.date)
                        .whereLessThanOrEqualTo("created_at", input.date.endOfDay())
                        .orderBy("created_at", Query.Direction.DESCENDING).get()
                    else firestore.collection("orders").whereGreaterThanOrEqualTo("created_at", input.date)
                        .orderBy("created_at", Query.Direction.DESCENDING).get()

                    val data = future.get()

                    data.documents.mapNotNull { snap ->
                        try {
                            val order = snap.toObject(Order::class.java)
                            order?.apply {
                                id = snap.id
                                createdAt = (snap.data["created_at"] as com.google.cloud.Timestamp?)?.let {
                                    Instant.fromEpochSeconds(it.seconds, it.nanos)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is OrderRepositoryContract.Inputs.RefreshSummary -> {
            fetchWithCache(
                input = input,
                forceRefresh = input.forceRefresh,
                getValue = { it.summary },
                updateState = { OrderRepositoryContract.Inputs.UpdateSummary(it) },
                doFetch = {
                    val firestore = com.google.firebase.cloud.FirestoreClient.getFirestore()
//                    val serverTime = firestore.collection("nonexistent").get().get().readTime


                    val calendar = Calendar.getInstance()
                    calendar.time = Date()
                    calendar[Calendar.HOUR_OF_DAY] = 0
                    calendar[Calendar.MINUTE] = 0
                    calendar[Calendar.SECOND] = 0
                    calendar[Calendar.MILLISECOND] = 0
                    val today = calendar.time

                    val future = firestore.collection("orders")
                        .whereGreaterThan("created_at", today)
                        .orderBy("created_at").get()

                    val data = future.get()

                    data.documents.mapNotNull { snap ->
                        try {
                            val order = snap.toObject(Order::class.java)
                            order?.apply {
                                id = snap.id
                                createdAt = (snap.data["created_at"] as com.google.cloud.Timestamp?)?.let {
                                    Instant.fromEpochSeconds(it.seconds, it.nanos)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                },
            )
        }

        is OrderRepositoryContract.Inputs.UpdateSummary -> {
            updateState { it.copy(summary = input.summary) }
        }
    }
}
