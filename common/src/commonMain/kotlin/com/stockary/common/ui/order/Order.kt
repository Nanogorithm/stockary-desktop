package com.stockary.common.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading
import com.stockary.common.components.tableview.TableView
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.repository.order.model.OrderTable
import com.stockary.common.repository.order.model.toOrderTable
import org.koin.core.component.KoinComponent


class OrderPage : KoinComponent {
    @Composable
    fun Orders(
        injector: ComposeDesktopInjector
    ) {
        val viewModelScope = rememberCoroutineScope()
        val vm: OrderViewModel = remember(viewModelScope) { injector.orderViewModel(viewModelScope) }
        val uiState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(OrderContract.Inputs.Initialize)
        }

        Content(uiState) {
            vm.trySend(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content(
        uiState: OrderContract.State,
        postInput: (OrderContract.Inputs) -> Unit
    ) {
        val productContent = remember(uiState.orders) {
            mutableStateOf(uiState.orders.getCachedOrEmptyList().map { it.toOrderTable() })
        }
        val selectedOrder: MutableState<OrderTable?> = remember { mutableStateOf(null) }

        val onOrderSelect: (OrderTable) -> Unit = {
            selectedOrder.value = it
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Orders", fontSize = 32.sp, fontWeight = FontWeight.W600)
            Spacer(modifier = Modifier.height(32.dp))
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
                TableView(currentItem = selectedOrder,
                    content = productContent,
                    indexColumn = true,
                    indexColWidth = 48.dp,
                    onRowSelection = onOrderSelect,
                    actions = {
                        IconButton(onClick = {
                            uiState.orders.getCachedOrEmptyList().firstOrNull { _order ->
                                _order.id == it.id
                            }?.let {

                            }
                        }) {
                            Icon(Icons.Default.Preview, "Delete Product", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = {
                            uiState.orders.getCachedOrEmptyList().firstOrNull { _order ->
                                _order.id == it.id
                            }?.let {
                                it.id?.let {

                                }
                            }
                        }) {
                            Icon(Icons.Default.Edit, "Edit Product", tint = MaterialTheme.colorScheme.error)
                        }
                    })
            }
        }
    }
}
