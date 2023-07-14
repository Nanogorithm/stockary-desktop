package com.stockary.common.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import com.stockary.common.repository.customer.model.CustomerTable
import com.stockary.common.repository.customer.model.toCustomerTable
import org.koin.core.component.KoinComponent

class CustomerPage : KoinComponent {
    @Composable
    fun Customer(
        injector: ComposeDesktopInjector
    ) {
        val viewModelScope = rememberCoroutineScope()
        val vm: CustomerViewModel = remember(viewModelScope) { injector.customerViewModel(viewModelScope) }

        val uiState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(CustomerContract.Inputs.Initialize)
        }

        Content(uiState) {
            vm.trySend(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content(
        uiState: CustomerContract.State, postInput: (CustomerContract.Inputs) -> Unit
    ) {
        val customerContent = remember(uiState.customers) {
            mutableStateOf(uiState.customers.getCachedOrEmptyList().map { it.toCustomerTable() })
        }
        val selectedCustomer: MutableState<CustomerTable?> = remember { mutableStateOf(null) }

        val onCustomerSelect: (CustomerTable) -> Unit = {
            selectedCustomer.value = it
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Customers", fontSize = 32.sp, fontWeight = FontWeight.W600)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    Button(
                        onClick = {
                            postInput(CustomerContract.Inputs.GoCustomerType)
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.secondaryContainer)
                        )
                    ) {
                        Icon(Icons.Default.Category, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Type")
                    }
                    Button(onClick = {
                        postInput(CustomerContract.Inputs.GoAddNewCustomer)
                    }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Customer")
                    }
                }
            }

            if (uiState.customers !is Cached.NotLoaded && uiState.customers.isLoading()) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.customers.getCachedOrEmptyList().isEmpty()) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("Customer list is empty")
                }
            } else {
                TableView(currentItem = selectedCustomer,
                    content = customerContent,
                    indexColumn = true,
                    indexColWidth = 48.dp,
                    onRowSelection = onCustomerSelect,
                    actions = {
                        IconButton(onClick = {
                            uiState.customers.getCachedOrEmptyList().firstOrNull { _customer ->
                                _customer.uid == it.uid
                            }?.let {
                                it.uid?.let {
                                    postInput(CustomerContract.Inputs.GoEditCustomer(it))
                                }
                            }
                        }) {
                            Icon(Icons.Default.Edit, "Edit Product", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {
                            uiState.customers.getCachedOrEmptyList().firstOrNull { _customer ->
                                _customer.uid == it.uid
                            }?.let {
//                                postInput(CustomerContract.Inputs.Delete(it))
                            }
                        }) {
                            Icon(Icons.Default.Delete, "Delete Product", tint = MaterialTheme.colorScheme.error)
                        }
                    })
            }
        }
    }
}