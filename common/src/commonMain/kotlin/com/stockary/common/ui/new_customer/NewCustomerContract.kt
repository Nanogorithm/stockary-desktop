package com.stockary.common.ui.new_customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.SupabaseResource
import com.stockary.common.form_builder.*
import com.stockary.common.repository.customer.model.Profile
import com.stockary.common.repository.customer.model.Role

object NewCustomerContract {
    data class State(
        val loading: Boolean = false,
        val customerId: String? = null,
        val formState: FormState<BaseState<*>> = FormState(
            fields = listOf(
                TextFieldState(
                    name = Profile::email.name,
                    validators = listOf(Validators.Email()),
                ),
                TextFieldState(
                    name = Profile::phone.name,
                    validators = listOf(Validators.Required(), Validators.Phone()),
                ),
                TextFieldState(
                    name = Profile::name.name,
                    validators = listOf(
                        Validators.Required()
                    ),
                ),
                TextFieldState(
                    name = Profile::address.name,
                    validators = listOf(
                        Validators.Required()
                    ),
                ),
                ChoiceState(
                    name = Profile::role.name,
                    validators = listOf(
                        Validators.Required()
                    ),
                )
            )
        ),
        val savingResponse: SupabaseResource<Boolean> = SupabaseResource.Idle,
        val customer: SupabaseResource<Profile> = SupabaseResource.Idle,
        val customerType: Cached<List<Role>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        data class Initialize(val customerId: String?) : Inputs()
        data class GetCustomer(val customerId: String) : Inputs()
        data class UpdateCustomer(val customer: SupabaseResource<Profile>) : Inputs()
        object AddNew : Inputs()
        object Update : Inputs()
        object UpdateFormData : Inputs()
        data class UpdateSavingResponse(val savingResponse: SupabaseResource<Boolean>) : Inputs()

        data class FetchCustomerTypes(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypes(val customerTypes: Cached<List<Role>>) : Inputs()

        object GoBack : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
