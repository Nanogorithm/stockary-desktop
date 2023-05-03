package com.stockary.common.ui.new_types

import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.BaseState
import com.stockary.common.form_builder.FormState
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.form_builder.Validators
import com.stockary.common.repository.customer.model.Role

object NewCustomerTypeContract {
    data class State(
        val loading: Boolean = false,
        val formState: FormState<BaseState<*>> = FormState(
            fields = listOf(
                TextFieldState(
                    name = Role::title.name,
                    validators = listOf(Validators.Required()),
                ), TextFieldState(
                    name = Role::slug.name,
                    validators = listOf(Validators.Required()),
                )
            )
        ),
        val savingResponse: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val typeId: String? = null
    )

    sealed class Inputs {
        data class Initialize(val typeId: String?) : Inputs()
        object GoBack : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
