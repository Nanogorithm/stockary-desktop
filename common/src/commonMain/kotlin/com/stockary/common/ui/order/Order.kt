package com.stockary.common.ui.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.toCurrencyFormat
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
    private fun Content(uiState: OrderContract.State, postInput: (OrderContract.Inputs) -> Unit) {
        var search by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Orders", fontSize = 32.sp, fontWeight = FontWeight.W600)
            Spacer(modifier = Modifier.height(32.dp))
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
                    Box(
                        modifier = Modifier.width(200.dp).height(28.dp).clip(RoundedCornerShape(8.dp))
                            .background(Color.White), contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.Search, null, tint = Color(0x33000000))
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (search.isBlank()) {
                                    Text("Search", fontSize = 14.sp, color = Color(0x33000000))
                                }
                                BasicTextField(value = search, onValueChange = { search = it })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {

                    }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("New Order")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxSize().padding(end = 50.dp), colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF7F9FB), contentColor = contentColorFor(Color(0xFFF7F9FB))
                ), shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Name", fontSize = 16.sp, color = Color(0x66000000))
                    Spacer(modifier = Modifier.weight(1f))
                    //                    Text("Price", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000))
                    Text(
                        "Total Quantity", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000)
                    )
                    Text("Amount", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000))
                    Text("Actions", modifier = Modifier.width(181.dp), fontSize = 12.sp, color = Color(0x66000000))
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
                            Text("${_order.profile?.firstName} ${_order.profile?.lastName}")
                            Spacer(modifier = Modifier.weight(1f))
                            //                            Text("$79.49", modifier = Modifier.width(181.dp))
                            Text("${_order.orderItems.sumOf { it.quantity }}", modifier = Modifier.width(181.dp))
                            Text(_order.total.toCurrencyFormat(), modifier = Modifier.width(181.dp))
                            Row(
                                modifier = Modifier.width(181.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier.size(32.dp).clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondaryContainer).clickable {

                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Print,
                                        null,
                                        modifier = Modifier.size(24.dp),
                                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                                Box(
                                    modifier = Modifier.size(32.dp).clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.errorContainer).clickable {

                                        }, contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Visibility,
                                        null,
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                        Divider(color = Color(0xFFD9D9D9))
                    }
                }
            }
        }
    }
}
