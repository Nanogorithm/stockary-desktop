package com.stockary.common.di.injector

import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.router.AppScreen
import com.stockary.common.ui.app.AppViewModel
import com.stockary.common.ui.category.CategoryViewModel
import com.stockary.common.ui.customer.CustomerViewModel
import com.stockary.common.ui.home.HomeViewModel
import com.stockary.common.ui.new_category.NewCategoryViewModel
import com.stockary.common.ui.new_customer.NewCustomerViewModel
import com.stockary.common.ui.new_product.NewProductViewModel
import com.stockary.common.ui.new_types.NewCustomerTypeViewModel
import com.stockary.common.ui.order.OrderViewModel
import com.stockary.common.ui.order_details.OrderDetailsViewModel
import com.stockary.common.ui.product.ProductViewModel
import com.stockary.common.ui.summary.SummaryViewModel
import com.stockary.common.ui.types.TypeViewModel
import kotlinx.coroutines.CoroutineScope

interface ComposeDesktopInjector {
    fun router(): Router<AppScreen>

    fun appViewModel(
        coroutineScope: CoroutineScope
    ): AppViewModel

    fun homeViewModel(
        coroutineScope: CoroutineScope
    ): HomeViewModel

    fun orderViewModel(coroutineScope: CoroutineScope): OrderViewModel
    fun orderDetailsViewModel(coroutineScope: CoroutineScope): OrderDetailsViewModel
    fun summaryViewModel(coroutineScope: CoroutineScope): SummaryViewModel
    fun typeViewModel(coroutineScope: CoroutineScope): TypeViewModel

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

    fun newCustomerViewModel(
        coroutineScope: CoroutineScope
    ): NewCustomerViewModel

    fun newCustomerTypeViewModel(
        coroutineScope: CoroutineScope
    ): NewCustomerTypeViewModel
}
