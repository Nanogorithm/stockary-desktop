package com.stockary.common.ui.order

import com.copperleaf.ballast.InputHandler
import com.copperleaf.ballast.InputHandlerScope
import com.copperleaf.ballast.observeFlows
import com.copperleaf.ballast.postInput
import com.google.cloud.firestore.SetOptions
import com.stockary.common.SupabaseResource
import com.stockary.common.repository.order.OrderRepository
import com.stockary.common.ui.summary.pdfInvoiceForCustomers
import kotlinx.coroutines.flow.map

class OrderInputHandler(
    val orderRepository: OrderRepository
) : InputHandler<OrderContract.Inputs, OrderContract.Events, OrderContract.State> {
    override suspend fun InputHandlerScope<OrderContract.Inputs, OrderContract.Events, OrderContract.State>.handleInput(
        input: OrderContract.Inputs
    ) = when (input) {
        is OrderContract.Inputs.Initialize -> {
            postInput(OrderContract.Inputs.FetchOrders(forceRefresh = true))
        }

        is OrderContract.Inputs.GoBack -> {
            postEvent(OrderContract.Events.NavigateUp)
        }

        is OrderContract.Inputs.FetchOrders -> {
            observeFlows("FetchOrders") {
                listOf(
                    orderRepository.getOrders(
                        refreshCache = input.forceRefresh,
                        date = input.date,
                        isSingleDay = input.isSingleDay
                    ).map {
                        OrderContract.Inputs.UpdateOrders(it)
                    }
                )
            }
        }

        is OrderContract.Inputs.UpdateOrders -> {
            updateState { it.copy(orders = input.orders) }
        }

        is OrderContract.Inputs.GoDetails -> {
            postEvent(OrderContract.Events.NavigateDetails(input.orderId))
        }

        is OrderContract.Inputs.PrintInvoices -> {
            sideJob("PrintInvoicesForCustomer") {
                pdfInvoiceForCustomers(
                    fileName = System.currentTimeMillis().toString(),
                    orders = input.orders
                )
            }
        }

        is OrderContract.Inputs.EditOrder -> {
            //update order
            updateState { it.copy(orderUpdateStatus = SupabaseResource.Loading) }
            sideJob("UpdateOrderStatus") {
                val response = try {
                    val firestore = com.google.firebase.cloud.FirestoreClient.getFirestore()
                    firestore.collection("orders").document(input.order.id!!).set(
                        mapOf(
                            "status" to input.status
                        ), SetOptions.merge()
                    )
                    SupabaseResource.Success(input.order.copy(status = input.status))
                } catch (e: Exception) {
                    e.printStackTrace()
                    SupabaseResource.Error(e)
                }

                postInput(OrderContract.Inputs.UpdateOrderStatus(response))
            }
        }

        is OrderContract.Inputs.UpdateOrderStatus -> {
            //update modified order
            updateState { it.copy(orderUpdateStatus = input.order) }
        }
    }
}
