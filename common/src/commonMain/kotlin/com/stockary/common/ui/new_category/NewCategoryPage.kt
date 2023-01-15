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
import com.copperleaf.ballast.navigation.vm.Router
import com.stockary.common.SupabaseResource
import com.stockary.common.router.AppScreen
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class NewCategoryPage : KoinComponent {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun NewCategory(
        router: Router<AppScreen>
    ) {
        val viewModelScope = rememberCoroutineScope()
        val vm: NewCategoryViewModel = remember(viewModelScope) { get { parametersOf(viewModelScope, router) } }
        val vmState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(NewCategoryContract.Inputs.Initialize)
        }

        var categoryName by remember { mutableStateOf("") }
        var categoryDescription by remember { mutableStateOf("") }
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
                    when (val saving = vmState.response) {
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
                        viewModelScope.launch {
                            vm.trySend(
                                NewCategoryContract.Inputs.Save(
                                    title = categoryName, description = categoryDescription
                                )
                            )
                        }
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
                    viewModelScope.launch {
                        vm.trySend(NewCategoryContract.Inputs.GoBack)
                    }
                }) {
                    Text("BACK")
                }
            }
        }
    }
}