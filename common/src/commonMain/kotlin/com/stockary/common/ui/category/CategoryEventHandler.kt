package com.stockary.common.ui.category

import com.copperleaf.ballast.EventHandler
import com.copperleaf.ballast.EventHandlerScope

class CategoryEventHandler : EventHandler<
        CategoryContract.Inputs,
        CategoryContract.Events,
        CategoryContract.State> {
    override suspend fun EventHandlerScope<
            CategoryContract.Inputs,
            CategoryContract.Events,
            CategoryContract.State>.handleEvent(
        event: CategoryContract.Events
    ) = when (event) {
        is CategoryContract.Events.NavigateUp -> {

        }
    }
}
