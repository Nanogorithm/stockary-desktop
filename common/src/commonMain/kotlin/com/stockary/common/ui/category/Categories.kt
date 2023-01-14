package com.stockary.common.ui.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.copperleaf.ballast.repository.cache.Cached
import com.copperleaf.ballast.repository.cache.getCachedOrEmptyList
import com.copperleaf.ballast.repository.cache.isLoading
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class CategoryPage : KoinComponent {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Categories() {
        val viewModelScope = rememberCoroutineScope()
        val vm: CategoryViewModel = remember(viewModelScope) { get { parametersOf(viewModelScope) } }
        val vmState by vm.observeStates().collectAsState()

        LaunchedEffect(vm) {
            vm.trySend(CategoryContract.Inputs.Initialize)
        }

        var search by remember { mutableStateOf("") }

        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Text("Categories", fontSize = 32.sp, fontWeight = FontWeight.W600)
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
                    Box(
                        modifier = Modifier.width(200.dp).height(28.dp).clip(RoundedCornerShape(8.dp))
                            .background(Color.White), contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(Icons.Default.Search, null, tint = Color(0x33000000))
                            Box(contentAlignment = Alignment.CenterStart) {
                                if (search.isBlank()) {
                                    Text("Search", fontSize = 14.sp, color = Color(0x33000000))
                                }
                                BasicTextField(value = search, onValueChange = { search = it })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))

                    Button(onClick = {

                    }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Category")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ID", fontSize = 12.sp, color = Color(0x66000000), modifier = Modifier.width(100.dp))
                Text("Category Name", fontSize = 12.sp, color = Color(0x66000000))
                Spacer(modifier = Modifier.weight(1f))
                Text("No of Products", modifier = Modifier.width(181.dp), fontSize = 12.sp, color = Color(0x66000000))
                Text("Sort", modifier = Modifier.width(181.dp), fontSize = 12.sp, color = Color(0x66000000))
                Text("Actions", modifier = Modifier.width(181.dp), fontSize = 12.sp, color = Color(0x66000000))
            }
            Divider(color = Color.Black.copy(alpha = 0.20f))
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (vmState.categoryList !is Cached.NotLoaded && vmState.categoryList.isLoading()) {
                    item {
                        CircularProgressIndicator()
                    }
                }
                items(vmState.categoryList.getCachedOrEmptyList()) { _category ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#${_category.id}", modifier = Modifier.width(100.dp))
                        Text(_category.title, modifier = Modifier.weight(1f))
                        Text("${_category.products.size}", modifier = Modifier.width(181.dp))
                        Text("0", modifier = Modifier.width(181.dp))
                        Row(
                            modifier = Modifier.width(181.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondaryContainer),
                                contentAlignment = Alignment.Center
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
                                    .background(MaterialTheme.colorScheme.errorContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onErrorContainer)
                            }
                        }
                    }
                    Divider(color = Color.Black.copy(alpha = 0.05f))
                }
            }
        }
    }
}
