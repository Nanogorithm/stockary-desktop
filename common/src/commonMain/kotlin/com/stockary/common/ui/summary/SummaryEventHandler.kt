package com.stockary.common.ui.summary

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen

class SummaryEventHandler(router: Router<AppScreen>) : EventHandler<
        SummaryContract.Inputs,
        SummaryContract.Events,
        SummaryContract.State> {
    override suspend fun EventHandlerScope<
            SummaryContract.Inputs,
            SummaryContract.Events,
            SummaryContract.State>.handleEvent(
        event: SummaryContract.Events
    ) = when (event) {
        is SummaryContract.Events.NavigateUp -> {

        }
    }
}
