package com.stockary.common.ui.order_details

import com.stockary.common.repository.order.model.Order

public object OrderDetailsContract {
    public data class State(
        val loading: Boolean = false,
        val orderId: String? = null,
        val order: Order? = null
    )

    public sealed class Inputs {
        data class Initialize(val orderId: String) : Inputs()
        object GoBack : Inputs()
        data class GetOrder(val orderId: String) : Inputs()
        data class UpdateOrder(val order: Order?) : Inputs()
    }

    public sealed class Events {
        object NavigateUp : Events()
    }
}
