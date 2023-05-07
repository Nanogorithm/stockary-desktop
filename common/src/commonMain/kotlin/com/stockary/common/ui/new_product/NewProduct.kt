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
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading
import com.stockary.common.SupabaseResource
import com.stockary.common.components.FileChooser
import com.stockary.common.components.SearchableDropDown
import com.stockary.common.components.SelectUnitType
import com.stockary.common.components.TextInput
import com.stockary.common.currencySymbol
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.form_builder.ChoiceState
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.repository.product.model.Product
import com.stockary.common.storagePrefix
import org.koin.core.component.KoinComponent
import java.io.File

class NewProductPage : KoinComponent {
    @Composable
    fun NewProduct(
        injector: ComposeDesktopInjector, productId: String?
    ) {
        val viewModelScope = rememberCoroutineScope()
        val vm: NewProductViewModel = remember(viewModelScope) { injector.newProductViewModel(viewModelScope) }
        val uiState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(NewProductContract.Inputs.Initialize(productId))
        }

        Content(uiState) {
            vm.trySend(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content(
        uiState: NewProductContract.State, postInput: (NewProductContract.Inputs) -> Unit
    ) {
        val error = remember { mutableStateOf<String?>(null) }

        val titleState: TextFieldState = uiState.formState.getState(Product::title.name)
        val descriptionState: TextFieldState = uiState.formState.getState(Product::description.name)
        val photoState: TextFieldState = uiState.formState.getState("photo")
        val unitAmountState: TextFieldState = uiState.formState.getState(Product::unitAmount.name)

        val categoryState: ChoiceState = uiState.formState.getState(Product::category.name)
        val unitTypeState: ChoiceState = uiState.formState.getState(Product::unitType.name)

        LaunchedEffect(photoState.value) {
            if (photoState.value.isNotBlank() && !photoState.value.contains("https://")) {
                postInput(NewProductContract.Inputs.UploadPhoto(file = File(photoState.value)))
            }
        }

        Box {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
                Text(
                    if (uiState.productId != null) "Editing ${uiState.productId}" else "New Product",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.W600
                )
                Spacer(modifier = Modifier.height(38.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(34.dp)) {
                    Card(
                        modifier = Modifier.weight(4f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF8F8F8)
                        )
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                            Text("Information", fontSize = 24.sp, fontWeight = FontWeight.W600)
                            Spacer(modifier = Modifier.height(36.dp))
                            TextInput(
                                label = "Product name",
                                placeHolder = "Special biscuit",
                                state = titleState,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextInput(
                                label = "Description",
                                placeHolder = "This is popular item contains a,b,c",
                                state = descriptionState,
                                maxLines = 3,
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = 80.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextInput(
                                        label = "Unit",
                                        placeHolder = "5 kg, 2 piece",
                                        state = unitAmountState,
                                        modifier = Modifier.wrapContentHeight().weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    if (uiState.unitTypes !is Cached.NotLoaded && uiState.unitTypes.isLoading()) {
                                        CircularProgressIndicator()
                                    } else {
                                        Column(modifier = Modifier.wrapContentWidth().wrapContentHeight()) {
                                            SelectUnitType(
                                                modifier = Modifier.width(200.dp).height(60.dp),
                                                items = uiState.unitTypes.getCachedOrEmptyList(),
                                                state = unitTypeState
                                            )

                                            if (unitTypeState.hasError) {
                                                Text(
                                                    text = unitTypeState.errorMessage,
                                                    modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                                                    style = androidx.compose.material.MaterialTheme.typography.caption.copy(
                                                        color = androidx.compose.material.MaterialTheme.colors.error
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            FileChooser(state = photoState, uploadResponse = uiState.uploadResponse)
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
                                if (uiState.categoryList !is Cached.NotLoaded && uiState.categoryList.isLoading()) {
                                    item {
                                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                    }
                                    item { }
                                } else {
                                    val categories = uiState.categoryList.getCachedOrEmptyList()
                                    if (categories.isNotEmpty()) {
                                        item {
                                            Column(modifier = Modifier.fillMaxWidth()) {
                                                SearchableDropDown(
                                                    modifier = Modifier.fillMaxWidth().height(60.dp),
                                                    label = "Category",
                                                    items = categories,
                                                    state = categoryState
                                                )
                                                if (categoryState.hasError) {
                                                    Text(
                                                        text = categoryState.errorMessage,
                                                        modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                                                        style = androidx.compose.material.MaterialTheme.typography.caption.copy(
                                                            color = androidx.compose.material.MaterialTheme.colors.error
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        item { }
                                    }
                                }

                                if (uiState.customerType !is Cached.NotLoaded && uiState.customerType.isLoading()) {
                                    item {
                                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                                    }
                                    item { }
                                } else {
                                    uiState.customerType.getCachedOrEmptyList().forEach { _customerType ->
                                        item {
                                            Column {

                                                val priceState: TextFieldState =
                                                    uiState.formState.getState(_customerType.slug!!)

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    val typeName = _customerType.title ?: ""


                                                    TextField(
                                                        value = priceState.value,
                                                        onValueChange = {
                                                            priceState.change(it)
                                                        },
                                                        placeholder = {
                                                            Text(
                                                                typeName
                                                            )
                                                        },
                                                        singleLine = true,
                                                        colors = TextFieldDefaults.textFieldColors(
                                                            containerColor = Color.White,
                                                            textColor = contentColorFor(Color.White),
                                                            unfocusedIndicatorColor = Color.Transparent,
                                                            placeholderColor = Color(0xFF676767)
                                                        ),
                                                        leadingIcon = {
                                                            Text(
                                                                currencySymbol,
                                                                color = Color(0xFF1F1F1F),
                                                                fontWeight = FontWeight.W500
                                                            )
                                                        },
                                                        modifier = Modifier.weight(1f),
                                                        label = {
                                                            Text("$typeName Price")
                                                        },
                                                        isError = priceState.hasError
                                                    )
                                                }
                                                if (priceState.hasError) {
                                                    Text(
                                                        text = priceState.errorMessage,
                                                        modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                                                        style = androidx.compose.material.MaterialTheme.typography.caption.copy(
                                                            color = androidx.compose.material.MaterialTheme.colors.error
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                when (val saving = uiState.response) {
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

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (uiState.formState.validate()) {
                                error.value = null

                                if (uiState.productId != null) {
                                    postInput(
                                        NewProductContract.Inputs.Update
                                    )
                                } else {
                                    postInput(
                                        NewProductContract.Inputs.SaveAndContinue
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
                        postInput(NewProductContract.Inputs.GoBack)
                    }) {
                        Text("BACK")
                    }
                }
            }
            if (uiState.productId != null) {
                when (uiState.product) {
                    is SupabaseResource.Error -> {

                    }

                    SupabaseResource.Idle -> {

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