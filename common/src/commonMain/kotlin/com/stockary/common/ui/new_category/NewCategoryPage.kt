package com.stockary.common.ui.new_category

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
import com.stockary.common.di.injector.ComposeDesktopInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class NewCategoryPage : KoinComponent {
    @Composable
    fun NewCategory(
        injector: ComposeDesktopInjector
    ) {
        val viewModelScope = rememberCoroutineScope()
        val vm: NewCategoryViewModel = remember(viewModelScope) { injector.newCategoryViewModel(viewModelScope) }
        val uiState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(NewCategoryContract.Inputs.Initialize)
        }

        Content(uiState) {
            vm.trySend(it)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun Content(
        uiState: NewCategoryContract.State,
        postInput: (NewCategoryContract.Inputs) -> Unit
    ) {
        var categoryName by remember { mutableStateOf("") }
        var categoryDescription by remember { mutableStateOf("") }
        var noteApplicable by remember { mutableStateOf(false) }
        var sortIndex by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 28.dp)) {
            Text("Add Category", fontSize = 32.sp, fontWeight = FontWeight.W600)
            Spacer(modifier = Modifier.height(38.dp))
            Card(
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F8F8)
                )
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                    Text("Information", fontSize = 24.sp, fontWeight = FontWeight.W600)
                    Spacer(modifier = Modifier.height(36.dp))
                    TextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        placeholder = { Text("Category Name") },
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
                    TextField(
                        value = categoryDescription,
                        onValueChange = { categoryDescription = it },
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Order note")
                        Switch(
                            checked = noteApplicable,
                            onCheckedChange = {
                                noteApplicable = it
                            }
                        )
                    }

                    when (val saving = uiState.response) {
                        is SupabaseResource.Error -> {
                            Text(saving.exception.message ?: "")
                        }

                        SupabaseResource.Idle -> {

                        }

                        SupabaseResource.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        }

                        is SupabaseResource.Success -> {
                            Text("Saved successfully")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        postInput(
                            NewCategoryContract.Inputs.Save(
                                title = categoryName,
                                description = categoryDescription,
                                noteApplicable = noteApplicable
                            )
                        )
                    }, shape = RoundedCornerShape(15.dp), enabled = categoryName.isNotBlank()
                ) {
                    Text("Save")
                }/*Button(
                        onClick = {
                            viewModelScope.launch {
                                vm.trySend(
                                    NewCategoryContract.Inputs.Save(
                                        title = categoryName, description = categoryDescription
                                    )
                                )
                            }
                        }, shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        ), enabled = categoryName.isNotBlank()
                    ) {
                        Text("Save & Continue")
                    }*/
                TextButton(onClick = {
                    postInput(NewCategoryContract.Inputs.GoBack)
                }) {
                    Text("BACK")
                }
            }
        }
    }
}