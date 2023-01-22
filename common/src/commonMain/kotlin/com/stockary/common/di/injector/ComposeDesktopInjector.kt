package com.stockary.common.di.injector

import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen
import com.stockary.common.ui.category.CategoryViewModel
import com.stockary.common.ui.new_product.NewProductViewModel
import com.stockary.common.ui.product.ProductViewModel
import kotlinx.coroutines.CoroutineScope

interface ComposeDesktopInjector {
    fun router(): Router<AppScreen>

    fun categoryViewModel(
        coroutineScope: CoroutineScope,
    ): CategoryViewModel

    fun productViewModel(
        coroutineScope: CoroutineScope,
    ): ProductViewModel

    fun newProductViewModel(
        coroutineScope: CoroutineScope,
    ): NewProductViewModel
}