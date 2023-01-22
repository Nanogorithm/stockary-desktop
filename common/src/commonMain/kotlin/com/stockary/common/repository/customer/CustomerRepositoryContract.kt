package com.stockary.common.repository.customer

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.customer.model.Profile

object CustomerRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val dataListInitialized: Boolean = false,
        val dataList: Cached<List<Profile>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshCustomerList(val forceRefresh: Boolean) : Inputs()
        data class CustomerListUpdated(val customerList: Cached<List<Profile>>) : Inputs()
    }
}
