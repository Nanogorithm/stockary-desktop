package com.stockary.common.ui.order_details

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.postInput
import com.google.firebase.cloud.FirestoreClient
import com.stockary.common.repository.order.OrderRepository
import com.stockary.common.repository.order.model.Order
import kotlinx.datetime.Instant

public class OrderDetailsInputHandler(
    val orderRepository: OrderRepository
) : InputHandler<
        OrderDetailsContract.Inputs,
        OrderDetailsContract.Events,
        OrderDetailsContract.State> {
    override suspend fun InputHandlerScope<
            OrderDetailsContract.Inputs,
            OrderDetailsContract.Events,
            OrderDetailsContract.State>.handleInput(
        input: OrderDetailsContract.Inputs
    ) = when (input) {
        is OrderDetailsContract.Inputs.Initialize -> {
            updateState { it.copy(orderId = input.orderId, loading = true) }
            postInput(OrderDetailsContract.Inputs.GetOrder(input.orderId))
        }

        is OrderDetailsContract.Inputs.GoBack -> {
            postEvent(OrderDetailsContract.Events.NavigateUp)
        }

        is OrderDetailsContract.Inputs.GetOrder -> {
            sideJob("GetOrder") {
                //get order
                val firestore = FirestoreClient.getFirestore()

                val order = try {
                    val future = firestore.collection("orders").document(input.orderId).get()
                    val data = future.get()
                    val order = data.toObject(Order::class.java)
                    order?.apply {
                        id = data.id
                        createdAt = (data["created_at"] as com.google.cloud.Timestamp?)?.let {
                            Instant.fromEpochSeconds(it.seconds, it.nanos)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
                postInput(OrderDetailsContract.Inputs.UpdateOrder(order))
            }
        }

        is OrderDetailsContract.Inputs.UpdateOrder -> {
            updateState { it.copy(order = input.order, loading = false) }
        }
    }
}
