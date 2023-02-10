package com.stockary.common.ui.new_customer

import com.stockary.common.form_builder.BaseState
import com.stockary.common.form_builder.FormState
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.form_builder.Validators
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.product.model.Product

object NewCustomerContract {
    data class State(
        val loading: Boolean = false,
        val formState: FormState<BaseState<*>> = FormState(
            fields = listOf(
                TextFieldState(
                    name = Profile::email.name,
                    validators = listOf(Validators.Required()),
                )
            )
        )
    )

    sealed class Inputs {
        data class Initialize(val customerId: Int?) : Inputs()
        object GoBack : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
