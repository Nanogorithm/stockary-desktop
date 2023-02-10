package com.stockary.common.ui.new_customer

import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.BaseState
import com.stockary.common.form_builder.FormState
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.form_builder.Validators
import io.github.jan.supabase.gotrue.providers.builtin.Email

object NewCustomerContract {
    data class State(
        val loading: Boolean = false, val customerId: Int? = null, val formState: FormState<BaseState<*>> = FormState(
            fields = listOf(
                TextFieldState(
                    name = "email",
                    validators = listOf(Validators.Required()),
                ),
                TextFieldState(
                    name = "password",
                    validators = listOf(
                        Validators.Required(),
                        Validators.Min(8, message = "minimum 8 characters required")
                    ),
                )
            )
        ), val savingResponse: SupabaseResource<Email.Result> = SupabaseResource.Idle
    )

    sealed class Inputs {
        data class Initialize(val customerId: Int?) : Inputs()
        object AddNew : Inputs()
        object Update : Inputs()
        data class UpdateSavingResponse(val savingResponse: SupabaseResource<Email.Result>) : Inputs()
        object GoBack : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
