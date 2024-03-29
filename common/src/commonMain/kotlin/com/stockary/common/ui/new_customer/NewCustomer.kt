package com.stockary.common.ui.new_customer

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.stockary.common.components.SelectCustomerType
import com.stockary.common.components.TextInput
import com.stockary.common.di.injector.ComposeDesktopInjector
import com.stockary.common.form_builder.ChoiceState
import com.stockary.common.form_builder.TextFieldState
import com.stockary.common.repository.customer.model.Profile
import org.koin.core.component.KoinComponent

class NewCustomerPage : KoinComponent {
    @Composable
    fun NewCustomer(
        injector: ComposeDesktopInjector, customerId: String?
    ) {
        val viewModelScope = rememberCoroutineScope()
        val vm: NewCustomerViewModel = remember(viewModelScope) { injector.newCustomerViewModel(viewModelScope) }
        val uiState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(NewCustomerContract.Inputs.Initialize(customerId))
        }

        Content(uiState) {
            vm.trySend(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content(
        uiState: NewCustomerContract.State, postInput: (NewCustomerContract.Inputs) -> Unit
    ) {
        val emailState: TextFieldState = uiState.formState.getState(Profile::email.name)
        val nameState: TextFieldState = uiState.formState.getState(Profile::name.name)
        val phoneState: TextFieldState = uiState.formState.getState(Profile::phone.name)
        val addressState: TextFieldState = uiState.formState.getState(Profile::address.name)
        val roleState: ChoiceState = uiState.formState.getState(Profile::role.name)

        val stateVertical = rememberScrollState(0)

        Box {
            Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp).verticalScroll(stateVertical)) {
                Text(
                    if (uiState.customerId != null) "Editing ${uiState.customerId}" else "New Customer",
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
                                label = "Phone",
                                placeHolder = "+8801738312933",
                                state = phoneState,
                                modifier = Modifier.width(300.dp)
                            )
                            Spacer(modifier = Modifier.height(36.dp))
                            TextInput(
                                label = "Email",
                                placeHolder = "jhon@gmail.com",
                                state = emailState,
                                modifier = Modifier.width(300.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextInput(
                                label = "Name", placeHolder = "", state = nameState, modifier = Modifier.width(300.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TextInput(
                                label = "Address",
                                placeHolder = "",
                                state = addressState,
                                modifier = Modifier.width(300.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Column(modifier = Modifier.width(300.dp)) {
                                if (uiState.customerType !is Cached.NotLoaded && uiState.customerType.isLoading()) {
                                    CircularProgressIndicator()
                                } else {
                                    SelectCustomerType(
                                        modifier = Modifier.fillMaxWidth().height(60.dp),
                                        items = uiState.customerType.getCachedOrEmptyList(),
                                        state = roleState
                                    )
                                    if (roleState.hasError) {
                                        Text(
                                            text = roleState.errorMessage,
                                            modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                                            style = androidx.compose.material.MaterialTheme.typography.caption.copy(
                                                color = androidx.compose.material.MaterialTheme.colors.error
                                            )
                                        )
                                    }
                                }

                            }
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
                            emailState.change("")
                            phoneState.change("")
                            addressState.change("")
                            nameState.change("")
                            roleState.change("")
                        }
                        Spacer(modifier = Modifier.height(28.dp))
                        Text("Customer ${if (uiState.customerId != null) "updated" else "registered"} successfully")
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            if (uiState.formState.validate()) {
                                if (uiState.customerId != null) {
                                    postInput(
                                        NewCustomerContract.Inputs.Update
                                    )
                                } else {
                                    postInput(
                                        NewCustomerContract.Inputs.AddNew
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
                        postInput(NewCustomerContract.Inputs.GoBack)
                    }) {
                        Text("BACK")
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(stateVertical)
            )
        }
    }
}