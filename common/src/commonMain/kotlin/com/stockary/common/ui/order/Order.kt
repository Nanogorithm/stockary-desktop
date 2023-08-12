package com.stockary.common.ui.order

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading
import com.stockary.common.SupabaseResource
import com.stockary.common.components.datapicker.DatePickerUI
import com.stockary.common.components.tableview.TableView
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.last7Days
import com.stockary.common.repository.order.model.Order
import com.stockary.common.repository.order.model.OrderTable
import com.stockary.common.repository.order.model.toOrderTable
import com.stockary.common.today
import com.stockary.common.yesterday
import org.koin.core.component.KoinComponent
import java.util.*


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
        var selectedFilter by remember { mutableStateOf("today") }
        var showDatePicker by remember { mutableStateOf(false) }
        var showOrderEditDialog by remember { mutableStateOf(false) }
        var editingOrder by remember { mutableStateOf<Order?>(null) }

        val productContent = remember(uiState.orders) {
            mutableStateOf(uiState.orders.getCachedOrEmptyList().map { it.toOrderTable() })
        }
        val selectedOrder: MutableState<OrderTable?> = remember { mutableStateOf(null) }

        val onOrderSelect: (OrderTable) -> Unit = {
            selectedOrder.value = it
        }

        val selectedDate = remember { mutableStateOf<Date?>(null) }

        LaunchedEffect(selectedDate.value) {
            selectedDate.value?.let {
                postInput(
                    OrderContract.Inputs.FetchOrders(
                        forceRefresh = true,
                        date = it,
                        isSingleDay = selectedFilter == "custom" || selectedFilter == "today" || selectedFilter == "yesterday"
                    )
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(end = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7F9FB), contentColor = contentColorFor(Color(0xFFF7F9FB))
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Orders",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.W600,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
                ) {
                    var expandMenu by remember { mutableStateOf(false) }
                    Box {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable {
                                    expandMenu = true
                                }
                        ) {
                            Text("By ${selectedFilter.capitalize()}", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.ArrowDropDown, null)
                        }

                        DropdownMenu(
                            expanded = expandMenu,
                            onDismissRequest = {
                                expandMenu = false
                            }
                        ) {
                            DropdownMenuItem(
                                onClick = {
                                    expandMenu = false
                                    selectedFilter = "today"
                                    selectedDate.value = today()
                                }
                            ) {
                                Text("Today")
                            }

                            DropdownMenuItem(
                                onClick = {
                                    expandMenu = false
                                    selectedFilter = "yesterday"
                                    selectedDate.value = yesterday()
                                }
                            ) {
                                Text("Yesterday")
                            }
                            DropdownMenuItem(
                                onClick = {
                                    expandMenu = false
                                    selectedFilter = "1week"
                                    selectedDate.value = last7Days()
                                }
                            ) {
                                Text("Last 7 days")
                            }
                            DropdownMenuItem(
                                onClick = {
                                    if (selectedFilter != "custom") {
                                        showDatePicker = true
                                    }
                                    expandMenu = false
                                    selectedFilter = "custom"
                                }
                            ) {
                                Text("Custom")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            val orders = uiState.orders.getCachedOrEmptyList()
                            if (orders.isNotEmpty()) {
                                postInput(OrderContract.Inputs.PrintInvoices(orders))
                            }
                        }
                    ) {
                        Icon(Icons.Default.Print, null)
                    }
                }
            }

            Dialog(
                visible = showDatePicker,
                onCloseRequest = {
                    showDatePicker = false
                },
                title = "Select date",
                undecorated = true,
                transparent = true
            ) {
                DatePickerUI(
                    label = "Select date",
                    onDismissRequest = {
                        showDatePicker = false
                        selectedDate.value = it
                    },
                )
            }


            LaunchedEffect(uiState.orderUpdateStatus) {
                if (uiState.orderUpdateStatus is SupabaseResource.Success) {
                    showOrderEditDialog = false
                    editingOrder = null
                }
            }

            Dialog(
                visible = showOrderEditDialog,
                onCloseRequest = {
                    showOrderEditDialog = false
                },
                title = "Edit order",
                undecorated = true,
                transparent = true
            ) {
                Card(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp)) {
                        var expandOrderStatus by remember { mutableStateOf(false) }
                        var selectedStatus by remember(editingOrder) { mutableStateOf(editingOrder?.status ?: "") }

                        Box(
                            modifier = Modifier.fillMaxWidth().wrapContentHeight()
                        ) {
                            Column {
                                Text("Order Status")
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        .clickable {
                                            expandOrderStatus = true
                                        }
                                ) {
                                    Text(selectedStatus.capitalize(), fontSize = 14.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.ArrowDropDown, null)
                                }
                            }

                            DropdownMenu(
                                expanded = expandOrderStatus,
                                onDismissRequest = {
                                    expandOrderStatus = false
                                }
                            ) {
                                DropdownMenuItem(
                                    onClick = {
                                        expandOrderStatus = false
                                        selectedStatus = "pending"
                                    }
                                ) {
                                    Text("Pending")
                                }

                                DropdownMenuItem(
                                    onClick = {
                                        expandOrderStatus = false
                                        selectedStatus = "processing"
                                    }
                                ) {
                                    Text("Processing")
                                }

                                DropdownMenuItem(
                                    onClick = {
                                        expandOrderStatus = false
                                        selectedStatus = "shipped"
                                    }
                                ) {
                                    Text("Shipped")
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        expandOrderStatus = false
                                        selectedStatus = "delivered"
                                    }
                                ) {
                                    Text("Delivered")
                                }
                                DropdownMenuItem(
                                    onClick = {
                                        expandOrderStatus = false
                                        selectedStatus = "canceled"
                                    }
                                ) {
                                    Text("Canceled")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Button(
                                onClick = {
                                    showOrderEditDialog = false
                                }
                            ) {
                                Text("Cancel")
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            when (uiState.orderUpdateStatus) {
                                is SupabaseResource.Error, SupabaseResource.Idle -> {
                                    Button(
                                        onClick = {
                                            editingOrder?.let {
                                                if (selectedStatus.isNotBlank() && selectedStatus != it.status) {
                                                    postInput(
                                                        OrderContract.Inputs.EditOrder(
                                                            order = it,
                                                            status = selectedStatus
                                                        )
                                                    )
                                                } else {
                                                    showOrderEditDialog = false
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Save")
                                    }
                                }

                                SupabaseResource.Loading -> {
                                    CircularProgressIndicator()
                                }

                                is SupabaseResource.Success -> {

                                }
                            }
                        }
                    }
                }
            }

            if (selectedFilter == "custom") {
                selectedDate.value?.let {
                    Text("Orders for $it")
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
                                it.id?.let {
                                    postInput(OrderContract.Inputs.GoDetails(orderId = it))
                                }
                            }
                        }) {
                            Icon(Icons.Default.Preview, "View Order", tint = MaterialTheme.colorScheme.primary)
                        }

                        IconButton(onClick = {
                            uiState.orders.getCachedOrEmptyList().firstOrNull { _order ->
                                _order.id == it.id
                            }?.let {
                                editingOrder = it
                                showOrderEditDialog = true
                            }
                        }) {
                            Icon(Icons.Default.Edit, "Change Order Status", tint = MaterialTheme.colorScheme.error)
                        }
                    })
            }
        }
    }
}
