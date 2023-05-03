package com.stockary.common.ui.types

import com.copperleaf.ballast.repository.cache.Cached
import com.stockary.common.repository.customer.model.Role

object TypeContract {
    data class State(
        val loading: Boolean = false, val types: Cached<List<Role>> = Cached.NotLoaded()
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()
        data class GoNewCustomerType(val typeId: String?) : Inputs()
        data class RefreshCustomerTypeList(val forceRefresh: Boolean) : Inputs()
        data class UpdateCustomerTypeList(val types: Cached<List<Role>>) : Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
        data class NavigateNewCustomerType(val typeId: String?) : Events()
    }
}
