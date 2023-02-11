package com.stockary.common.ui.new_customer

import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.BaseState
import com.stockary.common.form_builder.FormState
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.form_builder.Validators

object NewCustomerContract {
    data class State(
        val loading: Boolean = false, val customerId: Int? = null, val formState: FormState<BaseState<*>> = FormState(
            fields = listOf(
                TextFieldState(
                    name = "email",
                    validators = listOf(Validators.Required(), Validators.Email()),
                ), TextFieldState(
                    name = "name",
                    validators = listOf(
                        Validators.Required()
                    ),
                ), TextFieldState(
                    name = "address",
                    validators = listOf(
                        Validators.Required()
                    ),
                ), TextFieldState(
                    name = "role",
                    validators = listOf(
                        Validators.Required()
                    ),
                )
            )
        ),
        val savingResponse: SupabaseResource<Boolean> = SupabaseResource.Idle
    )

    sealed class Inputs {
        data class Initialize(val customerId: Int?) : Inputs()
        object AddNew : Inputs()
        object Update : Inputs()
        data class UpdateSavingResponse(val savingResponse: SupabaseResource<Boolean>) : Inputs()
        object GoBack : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
