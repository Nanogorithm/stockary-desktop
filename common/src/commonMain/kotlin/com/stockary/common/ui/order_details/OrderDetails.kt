package com.stockary.common.ui.order_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockary.common.di.injector.ComposeDesktopInjector

@Composable
fun OrderDetailsScreen(
    injector: ComposeDesktopInjector,
    orderId: String
) {
    val viewModelScope = rememberCoroutineScope()
    val vm: OrderDetailsViewModel = remember(viewModelScope) { injector.orderDetailsViewModel(viewModelScope) }
    val uiState by vm.observeStates().collectAsState()

    LaunchedEffect(vm, orderId) {
        vm.trySend(OrderDetailsContract.Inputs.Initialize(orderId = orderId))
    }

    Content(uiState) {
        vm.trySend(it)
    }
}

@Composable
private fun Content(
    uiState: OrderDetailsContract.State,
    postInput: (OrderDetailsContract.Inputs) -> Unit
) {
    Box {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text("Orders", fontSize = 32.sp, fontWeight = FontWeight.W600)
        }
    }
}