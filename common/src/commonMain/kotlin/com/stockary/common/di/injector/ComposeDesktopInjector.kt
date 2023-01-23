package com.stockary.common.di.injector

import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen
import com.stockary.common.ui.app.AppViewModel
import com.stockary.common.ui.category.CategoryViewModel
import com.stockary.common.ui.customer.CustomerViewModel
import com.stockary.common.ui.new_category.NewCategoryViewModel
import com.stockary.common.ui.new_product.NewProductViewModel
import com.stockary.common.ui.product.ProductViewModel
import kotlinx.coroutines.CoroutineScope

interface ComposeDesktopInjector {
    fun router(): Router<AppScreen>

    fun appViewModel(
        coroutineScope: CoroutineScope
    ):AppViewModel

    fun categoryViewModel(
        coroutineScope: CoroutineScope,
    ): CategoryViewModel

    fun productViewModel(
        coroutineScope: CoroutineScope,
    ): ProductViewModel

    fun customerViewModel(coroutineScope: CoroutineScope): CustomerViewModel

    fun newProductViewModel(
        coroutineScope: CoroutineScope,
    ): NewProductViewModel
    fun newCategoryViewModel(
        coroutineScope: CoroutineScope,
    ): NewCategoryViewModel
}
