package com.stockary.common.ui.customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.customer.model.Profile

object CustomerContract {
    data class State(
        val loading: Boolean = false, val customers: Cached<List<Profile>> = Cached.NotLoaded()
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        object GoAddNewCustomer : Inputs()
        data class GoEditCustomer(val customerId: String) : Inputs()
        object GoCustomerType : Inputs()
        data class FetchCustomerList(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerList(val customers: Cached<List<Profile>>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
        object NavigateCustomerType : Events()
        object NavigateAddNewCustomer : Events()
        data class NavigateEditCustomer(val customerId: String) : Events()
    }
}
