package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.*
import com.stockary.common.repository.customer.model.Role

object NewCustomerContract {
    data class State(
        val loading: Boolean = false, val customerId: Int? = null,
        val formState: FormState<BaseState<*>> = FormState(
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
                ), ChoiceState(
                    name = "role",
                    validators = listOf(
                        Validators.Required()
                    ),
                )
            )
        ),
        val savingResponse: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val customerType: Cached<List<Role>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        data class Initialize(val customerId: Int?) : Inputs()
        object AddNew : Inputs()
        object Update : Inputs()
        data class UpdateSavingResponse(val savingResponse: SupabaseResource<Boolean>) : Inputs()

        data class FetchCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val customerTypes: Cached<List<Role>>) : Inputs()

        object GoBack : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
