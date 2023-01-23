package com.stockary.common.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.QueryStats
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
import com.stockary.common.toCurrencyFormat

@Composable
fun Overview(
    injector: ComposeDesktopInjector
) {
    val viewModelScope = rememberCoroutineScope()
    val vm: HomeViewModel = remember(viewModelScope) { injector.homeViewModel(viewModelScope) }
    val uiState by vm.observeStates().collectAsState()

    LaunchedEffect(vm) {
        vm.trySend(HomeContract.Inputs.Initialize)
    }

    Content(uiState) {
        vm.trySend(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(uiState: HomeContract.State, postInput: (HomeContract.Inputs) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(start = 64.dp)) {
        Text("Hello there, Shaad", fontSize = 32.sp, fontWeight = FontWeight.W600)
        Spacer(modifier = Modifier.height(38.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(44.dp)) {
            Card(
                modifier = Modifier.width(400.dp), colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F5FF), contentColor = contentColorFor(Color(0xFFE3F5FF))
                ), shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.QueryStats, null)
                        }
                        Text("Sales", fontSize = 24.sp, fontWeight = FontWeight.W500)
                        Text("Today", fontSize = 14.sp)
                    }
                    if (uiState.orders !is Cached.NotLoaded && uiState.orders.isLoading()) {
                        CircularProgressIndicator()
                    } else {
                        Text(
                            uiState.orders.getCachedOrEmptyList().sumOf { it.total.toDouble() }.toCurrencyFormat(),
                            fontSize = 32.sp
                        )
                    }
                }
            }
            Card(
                modifier = Modifier.width(400.dp), colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE2E3FF), contentColor = contentColorFor(Color(0xFFE2E3FF))
                ), shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Box(modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Dashboard, null)
                        }
                        Text("Orders", fontSize = 24.sp, fontWeight = FontWeight.W500)
                        Text("Today", fontSize = 14.sp)
                    }

                    if (uiState.orders !is Cached.NotLoaded && uiState.orders.isLoading()) {
                        CircularProgressIndicator()
                    } else {
                        Text(uiState.orders.getCachedOrEmptyList().size.toString(), fontSize = 32.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(48.dp))
        Card(
            modifier = Modifier.fillMaxSize().padding(end = 50.dp), colors = CardDefaults.cardColors(
                containerColor = Color(0xFFF7F9FB), contentColor = contentColorFor(Color(0xFFF7F9FB))
            ), shape = RoundedCornerShape(20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Top Selling Products",
                fontSize = 22.sp,
                fontWeight = FontWeight.W600,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Name", fontSize = 16.sp, color = Color(0x66000000))
                Spacer(modifier = Modifier.weight(1f))
                Text("Price", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000))
                Text("Quantity", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000))
                Text("Amount", modifier = Modifier.width(181.dp), fontSize = 16.sp, color = Color(0x66000000))
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
                        Text("ASOS Ridley Hight Waist")
                        Spacer(modifier = Modifier.weight(1f))
                        Text("$79.49", modifier = Modifier.width(181.dp))
                        Text("82", modifier = Modifier.width(181.dp))
                        Text("$6518.18", modifier = Modifier.width(181.dp))
                    }
                    Divider(color = Color(0xFFD9D9D9))
                }
            }
        }
    }
}