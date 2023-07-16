package com.stockary.common.ui.summary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading
import com.stockary.common.components.tableview.TableView
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.repository.order.model.OrderSummaryTable
import com.stockary.common.repository.order.model.toOrderSummaryItem

@Composable
fun Summary(injector: ComposeDesktopInjector) {
    val viewModelScope = rememberCoroutineScope()
    val vm: SummaryViewModel = remember(viewModelScope) { injector.summaryViewModel(viewModelScope) }
    val uiState by vm.observeStates().collectAsState()
    LaunchedEffect(vm) {
        vm.trySend(SummaryContract.Inputs.Initialize)
    }

    Content(uiState = uiState) {
        vm.trySend(it)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: SummaryContract.State, postInput: (SummaryContract.Inputs) -> Unit
) {

    var selectedFilter by remember { mutableStateOf("category") }

    val productContent = remember(uiState.orders) {
        mutableStateOf(
            uiState.orders.getCachedOrEmptyList().flatMap { _order ->
                _order.toOrderSummaryItem()
            }.groupBy {
                it.productId
            }.map {
                println("${it.key} => ${it.value.map { it.productName }}")
                val totalUnitAmount = it.value.map { item -> (item.units?.amount ?: 0f) * item.quantity }.sum()
                val first = it.value.firstOrNull()

                OrderSummaryTable(
                    userId = null,
                    customerName = null,
                    productName = first?.productName,
                    categoryName = first?.category,
                    totalUnit = totalUnitAmount,
                    unitName = first?.units?.type ?: ""
                )
            }.sortedBy {
                it.categoryName
            }
        )
    }
    val selectedOrder: MutableState<OrderSummaryTable?> = remember { mutableStateOf(null) }

    val onOrderSelect: (OrderSummaryTable) -> Unit = {
        selectedOrder.value = it
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(start = 64.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(end = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF7F9FB), contentColor = contentColorFor(Color(0xFFF7F9FB))
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Order Summary",
                fontSize = 22.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
            ) {
                var expandMenu by remember { mutableStateOf(false) }
                Box {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp).clickable {
                        expandMenu = true
                    }) {
                        Text("By ${selectedFilter.capitalize()}", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowDropDown, null)
                    }

                    DropdownMenu(expanded = expandMenu, onDismissRequest = {
                        expandMenu = false
                    }) {
                        DropdownMenuItem(onClick = {
                            expandMenu = false
                            selectedFilter = "category"
                        }) {
                            Text("Category")
                        }

                        DropdownMenuItem(
                            onClick = {
                                expandMenu = false
                                selectedFilter = "customer"
                            }
                        ) {
                            Text("Customer")
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    postInput(SummaryContract.Inputs.Print(uiState.orders.getCachedOrEmptyList()))
                }) {
                    Icon(Icons.Default.Print, null)
                }
            }
        }

        if (uiState.orders !is Cached.NotLoaded && uiState.orders.isLoading()) {
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.orders.getCachedOrEmptyList().isEmpty()) {
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text("Product list is empty")
            }
        } else {
            TableView(
                currentItem = selectedOrder,
                content = productContent,
                indexColumn = true,
                indexColWidth = 48.dp,
                onRowSelection = onOrderSelect,
                actions = null
            )
        }
    }
}