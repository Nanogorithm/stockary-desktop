package com.stockary.common.ui.order_details

import androidx.compose.animation.core.tween
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.toCurrencyFormat
import com.stockary.common.toDateTimeString
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import kotlinx.datetime.toJavaInstant

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: OrderDetailsContract.State,
    postInput: (OrderDetailsContract.Inputs) -> Unit
) {
    val stateVertical = rememberScrollState(0)

    Box {
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(horizontal = 16.dp).verticalScroll(stateVertical)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Order #${uiState.orderId}", fontSize = 32.sp, fontWeight = FontWeight.W600)
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        uiState.order?.let {
                            postInput(OrderDetailsContract.Inputs.PrintInvoice(it))
                        }
                    }
                ) {
                    Icon(Icons.Default.Print, null)
                }
            }
            if (uiState.loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                if (uiState.order == null) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("Order not found for ${uiState.orderId}")
                    }
                }
            }

            uiState.order?.let {
                Text("Customer Name: ${it.customer_name}")
                Text("Status: ${it.status}")
                Text("Time: ${it.createdAt?.toJavaInstant()?.toDateTimeString()}")
                Text("Total: ${it.total.toCurrencyFormat()}")
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Name", modifier = Modifier.weight(3f), textAlign = TextAlign.Center)
                        Text("Price", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Demand", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Total", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }
                Spacer(Modifier.height(8.dp))

                it.items.forEach {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(3f)) {
                                Text(it.title ?: "")
                                Spacer(Modifier.height(8.dp))
                                it.note?.let {
                                    if (it.photo != null) {
                                        //show with photo
                                        Row {
                                            it.photo?.let {
                                                val painterResource: Resource<Painter> =
                                                    lazyPainterResource(data = it)
                                                KamelImage(
                                                    resource = painterResource,
                                                    contentDescription = "Note photo",
                                                    onLoading = { progress -> CircularProgressIndicator(progress) },
                                                    onFailure = { exception ->
                                                        exception.printStackTrace()
                                                    },
                                                    animationSpec = tween(),
                                                    modifier = Modifier.size(100.dp)
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            it.text?.let {
                                                Text(it)
                                            }
                                        }
                                    } else {
                                        it.text?.let {
                                            Text(it)
                                        }
                                    }
                                }
                            }
                            Text(it.price.toCurrencyFormat(), modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                            Text(it.quantity.toString(), modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(
                                (it.quantity * it.price).toCurrencyFormat(),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
                Spacer(Modifier.height(50.dp))
            }
        }

        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(stateVertical)
        )
    }
}