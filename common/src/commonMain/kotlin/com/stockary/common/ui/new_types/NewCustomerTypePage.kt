package com.stockary.common.ui.new_types

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockary.common.SupabaseResource
import com.stockary.common.components.TextInput
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.repository.customer.model.Role

@Composable
fun NewCustomerTypePage(
    injector: ComposeDesktopInjector,
    typeId: String?
) {
    val viewModelScope = rememberCoroutineScope()
    val vm: NewCustomerTypeViewModel = remember(viewModelScope) { injector.newCustomerTypeViewModel(viewModelScope) }
    val uiState by vm.observeStates().collectAsState()

    LaunchedEffect(vm) {
        vm.trySend(NewCustomerTypeContract.Inputs.Initialize(typeId))
    }

    Content(uiState) {
        vm.trySend(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: NewCustomerTypeContract.State,
    postInput: (NewCustomerTypeContract.Inputs) -> Unit
) {
    val titleState: TextFieldState = uiState.formState.getState(Role::title.name)
    val slugState: TextFieldState = uiState.formState.getState(Role::slug.name)

    Box {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
            Text(
                if (uiState.typeId != null) "Editing ${uiState.typeId}" else "New Customer Type",
                fontSize = 32.sp,
                fontWeight = FontWeight.W600
            )
            Spacer(modifier = Modifier.height(38.dp))
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(34.dp)
            ) {
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
                            label = "Title",
                            placeHolder = "Dealer",
                            state = titleState,
                            modifier = Modifier.width(300.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        TextInput(
                            label = "Slug", placeHolder = "dealer", state = slugState, modifier = Modifier.width(300.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            when (val saving = uiState.savingResponse) {
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
                    LaunchedEffect(Unit) {
                        titleState.change("")
                        slugState.change("")
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                    Text("Customer type registered successfully")
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (uiState.formState.validate()) {

                        }
                    }, shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text("Save")
                }
                TextButton(onClick = {
                    postInput(NewCustomerTypeContract.Inputs.GoBack)
                }) {
                    Text("BACK")
                }
            }
        }
    }
}