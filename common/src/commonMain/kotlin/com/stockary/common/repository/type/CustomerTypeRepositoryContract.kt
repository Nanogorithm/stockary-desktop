package com.stockary.common.repository.type

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.customer.model.Role

object CustomerTypeRepositoryContract {
    data class State(
        val initialized: Boolean = false,

        val customerTypeListInitialized: Boolean = false,
        val customerTypeList: Cached<List<Role>> = Cached.NotLoaded(),
    )

    sealed class Inputs {
        object ClearCaches : Inputs()
        object Initialize : Inputs()
        object RefreshAllCaches : Inputs()

        data class RefreshCustomerTypeList(val forceRefresh: Boolean) : Inputs()
        data class CustomerTypeListUpdated(val customerList: Cached<List<Role>>) : Inputs()
    }
}
