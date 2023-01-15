package com.stockary.common.ui.new_product

import com.stockary.common.SupabaseResource

object NewProductContract {
    data class State(
        val loading: Boolean = false, val response: SupabaseResource<Boolean> = SupabaseResource.Idle
    )

    sealed class Inputs {
        object Initialize : Inputs()
        object GoBack : Inputs()

        data class Save(val title: String, val description: String) : Inputs()
        data class SaveAndContinue(val title: String, val description: String) : Inputs()
        data class UpdateSaveResponse(val response: SupabaseResource<Boolean>, val isContinue: Boolean = false) :
            Inputs()
    }

    sealed class Events {
        object NavigateUp : Events()
    }
}
