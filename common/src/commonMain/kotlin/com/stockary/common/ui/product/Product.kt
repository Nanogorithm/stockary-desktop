package com.stockary.common.ui.product

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
import com.helloanwar.common.ui.components.tableview.TableView
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.repository.product.model.ProductTable
import com.stockary.common.repository.product.model.toProductTable
import org.koin.core.component.KoinComponent

class ProductPage : KoinComponent {
    @Composable
    fun Product(
        injector: ComposeDesktopInjector
    ) {
        val viewModelCoroutineScope = rememberCoroutineScope()
        val vm = remember(viewModelCoroutineScope) { injector.productViewModel(viewModelCoroutineScope) }

        val uiState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(ProductContract.Inputs.Initialize)
        }

        Content(uiState) {
            vm.trySend(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content(
        uiState: ProductContract.State, postInput: (ProductContract.Inputs) -> Unit
    ) {
        val productContent = remember(uiState.products) {
            mutableStateOf(uiState.products.getCachedOrEmptyList().map { it.toProductTable() })
        }
        val selectedOrder: MutableState<ProductTable?> = remember { mutableStateOf(null) }

        val onOrderSelect: (ProductTable) -> Unit = {
            selectedOrder.value = it
        }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Products", fontSize = 32.sp, fontWeight = FontWeight.W600)

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
                            postInput(ProductContract.Inputs.GoCategory)
                        }, colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = contentColorFor(MaterialTheme.colorScheme.secondaryContainer)
                        )
                    ) {
                        Icon(Icons.Default.Category, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Category")
                    }
                    Button(onClick = {
                        postInput(ProductContract.Inputs.GoAddNew)
                    }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Product")
                    }
                }
            }

            if (uiState.products !is Cached.NotLoaded && uiState.products.isLoading()) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.products.getCachedOrEmptyList().isEmpty()) {
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
                            uiState.products.getCachedOrEmptyList().firstOrNull { _order ->
                                _order.id == it.id
                            }?.let {
                                it.id?.let {
                                    postInput(ProductContract.Inputs.GoEdit(it))
                                }
                            }
                        }) {
                            Icon(Icons.Default.Edit, "Edit Product", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = {
                            uiState.products.getCachedOrEmptyList().firstOrNull { _order ->
                                _order.id == it.id
                            }?.let {
                                postInput(ProductContract.Inputs.Delete(it))
                            }
                        }) {
                            Icon(Icons.Default.Delete, "Delete Product", tint = MaterialTheme.colorScheme.error)
                        }
                    })
            }

            /*

            LazyColumn(modifier = Modifier.fillMaxSize()) {

                if (uiState.products !is Cached.NotLoaded && uiState.products.isLoading()) {
                    item {
                        Spacer(modifier = Modifier.height(48.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator()
                        }
                    }
                } else if (uiState.products.getCachedOrEmptyList().isEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(48.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            Text("Product list is empty")
                        }
                    }
                }

                items(uiState.products.getCachedOrEmptyList()) { _product ->
                    val image = remember { mutableStateOf("") }
//                    LaunchedEffect(_product.photo) {
//                        _product.photo?.let {
//                            if (it.contains("https://")) {
//                                image.value = it
//                            } else {
//                                val bucket: Bucket? = StorageClient.getInstance().bucket()
//                                try {
//                                    val url = bucket?.get(it)?.signUrl(1, TimeUnit.HOURS)
//                                    println("public url => $url")
//                                    image.value = url.toString()
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                }
//                            }
//                        }
//                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#${_product.id?.substring(0, 6)}", modifier = Modifier.width(100.dp))
                        Box(modifier = Modifier.width(80.dp)) {
                            val painterResource: Resource<Painter> = lazyPainterResource(data = image.value)
                            KamelImage(
                                resource = painterResource,
                                contentDescription = "Product photo",
                                onLoading = { progress -> CircularProgressIndicator(progress) },
                                onFailure = { exception ->

                                },
                                animationSpec = tween(),
                                modifier = Modifier.height(80.dp).width(80.dp)
                            )
                        }
                        Text(_product.title, modifier = Modifier.weight(1f))
                        Text(_product.category ?: "", modifier = Modifier.width(181.dp))
                        Text("${_product.stock}", modifier = Modifier.width(181.dp))
                        Text(
                            "${_product.units?.amount?.removeEmptyFraction() ?: ""} ${_product.units?.type ?: ""}",
                            modifier = Modifier.width(181.dp)
                        )
                        Row(
                            modifier = Modifier.width(181.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer).clickable {
                                        postInput(ProductContract.Inputs.GoEdit(_product.id!!))
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Edit,
                                    null,
                                    modifier = Modifier.size(24.dp),
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.errorContainer).clickable {
                                        postInput(ProductContract.Inputs.Delete(product = _product))
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                    Divider(color = Color.Black.copy(alpha = 0.05f))
                }
            }*/
        }
    }
}