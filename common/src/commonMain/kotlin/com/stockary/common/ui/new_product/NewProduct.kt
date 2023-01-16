package com.stockary.common.ui.new_product

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.navigation.vm.Router
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading
import com.stockary.common.SupabaseResource
import com.stockary.common.components.SearchableDropDown
import com.stockary.common.currencySymbol
import com.stockary.common.repository.category.model.Category
import com.stockary.common.router.AppScreen
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class NewProductPage : KoinComponent {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NewProduct(
        router: Router<AppScreen>
    ) {
        val viewModelScope = rememberCoroutineScope()
        val vm: NewProductViewModel = remember(viewModelScope) { get { parametersOf(viewModelScope, router) } }
        val vmState by vm.observeStates().collectAsState()
        val selectedCategory = remember { mutableStateOf<Category?>(null) }
        val error = remember { mutableStateOf<String?>(null) }

        LaunchedEffect(vm) {
            vm.trySend(NewProductContract.Inputs.Initialize)
        }

        val prices = remember { mutableStateListOf<String>() }


        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
            Text("New Product", fontSize = 32.sp, fontWeight = FontWeight.W600)
            Spacer(modifier = Modifier.height(38.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(34.dp)) {
                Card(
                    modifier = Modifier.weight(4f), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F8F8)
                    )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                        Text("Information", fontSize = 24.sp, fontWeight = FontWeight.W600)
                        Spacer(modifier = Modifier.height(36.dp))
                        TextField(value = vmState.productName,
                            onValueChange = {
                                viewModelScope.launch {
                                    vm.trySend(NewProductContract.Inputs.NameChanged(it))
                                }
                            },
                            placeholder = { Text("Product Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                textColor = contentColorFor(Color.White),
                                unfocusedIndicatorColor = Color.Transparent,
                                placeholderColor = Color(0xFF676767)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(value = vmState.productDescription,
                            onValueChange = {
                                viewModelScope.launch {
                                    vm.trySend(NewProductContract.Inputs.DescriptionChanged(it))
                                }
                            },
                            placeholder = { Text("Description") },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 80.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                textColor = contentColorFor(Color.White),
                                unfocusedIndicatorColor = Color.Transparent,
                                placeholderColor = Color(0xFF676767)
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(value = vmState.basePrice,
                            onValueChange = {
                                viewModelScope.launch {
                                    vm.trySend(NewProductContract.Inputs.PriceChanged(it))
                                }
                            },
                            placeholder = { Text("Base Price") },
                            singleLine = true,
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.White,
                                textColor = contentColorFor(Color.White),
                                unfocusedIndicatorColor = Color.Transparent,
                                placeholderColor = Color(0xFF676767)
                            ),
                            leadingIcon = {
                                Text(
                                    currencySymbol, color = Color(0xFF1F1F1F), fontWeight = FontWeight.W500
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = {
                                Text("Base Price")
                            })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                Card(
                    modifier = Modifier.weight(3f), colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF8F8F8)
                    ), shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                        Text("Organize", fontSize = 24.sp, fontWeight = FontWeight.W600)
                        Spacer(modifier = Modifier.height(36.dp))
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (vmState.categoryList !is Cached.NotLoaded && vmState.categoryList.isLoading()) {
                                item {
                                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                }
                                item { }
                            } else {
                                val categories = vmState.categoryList.getCachedOrEmptyList()
                                if (categories.isNotEmpty()) {
                                    item {
                                        SearchableDropDown(
                                            modifier = Modifier.width(200.dp).height(60.dp),
                                            label = "Category",
                                            items = categories,
                                        ) {
                                            selectedCategory.value = it
                                        }
                                    }
                                    item { }
                                }
                            }

                            if (vmState.customerType !is Cached.NotLoaded && vmState.customerType.isLoading()) {
                                item {
                                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                }
                                item { }
                            } else {
                                vmState.customerType.getCachedOrEmptyList().forEachIndexed { index, _customerType ->
                                    item {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            val typeName = _customerType.name.replaceFirst(
                                                _customerType.name.first(), _customerType.name.first().uppercaseChar()
                                            )

                                            prices.add("")

                                            TextField(value = prices[index], onValueChange = {
                                                prices[index] = it
                                            }, placeholder = {
                                                Text(
                                                    typeName
                                                )
                                            }, singleLine = true, colors = TextFieldDefaults.textFieldColors(
                                                containerColor = Color.White,
                                                textColor = contentColorFor(Color.White),
                                                unfocusedIndicatorColor = Color.Transparent,
                                                placeholderColor = Color(0xFF676767)
                                            ), leadingIcon = {
                                                Text(
                                                    currencySymbol,
                                                    color = Color(0xFF1F1F1F),
                                                    fontWeight = FontWeight.W500
                                                )
                                            }, modifier = Modifier.weight(1f), label = {
                                                Text("$typeName Price")
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            when (val saving = vmState.response) {
                is SupabaseResource.Error -> {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(saving.exception.message ?: "")
                }

                SupabaseResource.Idle -> {

                }

                SupabaseResource.Loading -> {
                    Spacer(modifier = Modifier.height(28.dp))
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                is SupabaseResource.Success -> {
                    Spacer(modifier = Modifier.height(28.dp))
                    Text("Saved successfully")
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            error.value?.let {
                Text(it)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
//                Button(
//                    onClick = {
//
//                    }, shape = RoundedCornerShape(15.dp)
//                ) {
//                    Text("PUBLISH")
//                }

                Button(
                    onClick = {
                        if (selectedCategory.value == null) {
                            error.value = "Please select category first"
                        } else {
                            error.value = null
                            viewModelScope.launch {
                                vm.trySend(
                                    NewProductContract.Inputs.SaveAndContinue(
                                        prices = prices.toList(),
                                        category = selectedCategory.value!!,
                                        types = vmState.customerType.getCachedOrEmptyList()
                                    )
                                )
                            }
                        }
                    }, shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Save")
                }
                TextButton(onClick = {
                    viewModelScope.launch {
                        vm.trySend(NewProductContract.Inputs.GoBack)
                    }
                }) {
                    Text("BACK")
                }
            }
        }
    }
}