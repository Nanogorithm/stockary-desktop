package com.stockary.common.ui.summary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.stockary.common.di.injector.ComposeDesktopInjector

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
    Column(modifier = Modifier.fillMaxSize().padding(start = 64.dp)) {
        Spacer(modifier = Modifier.height(48.dp))
        Card(
            modifier = Modifier.fillMaxSize().padding(end = 50.dp), colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF7F9FB), contentColor = contentColorFor(Color(0xFFF7F9FB))
            ), shape = RoundedCornerShape(20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Order Summary",
                fontSize = 22.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                var selectedFilter by remember { mutableStateOf("category") }
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

                        DropdownMenuItem(onClick = {
                            expandMenu = false
                            selectedFilter = "customer"
                        }) {
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
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Category", fontSize = 16.sp, color = Color(0x66000000))
                Spacer(modifier = Modifier.weight(1f))
                Text("Item", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000))
                Text("Total Unit", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000))
            }
            Divider(color = Color(0x33000000))
            LazyColumn(modifier = Modifier.fillMaxSize()) {

                if (uiState.orders !is Cached.NotLoaded && uiState.orders.isLoading()) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }

                items(uiState.orders.getCachedOrEmptyList()) { _order ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(_order.categoryName)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(_order.title, modifier = Modifier.width(181.dp))
                        Text("${_order.totalUnit} ${_order.unitName}", modifier = Modifier.width(181.dp))
                    }
                    Divider(color = Color(0xFFD9D9D9))
                }
            }
        }
    }
}