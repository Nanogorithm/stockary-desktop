package com.stockary.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stockary.common.screen.*

val NavItems = listOf("Overview", "Products", "Customers", "Orders", "New Product", "New Category", "Categories")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val scope = rememberCoroutineScope()
    val selectedItem = remember { mutableStateOf(6) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPaddings ->
        PermanentNavigationDrawer(
            drawerContent = {
                Spacer(modifier = Modifier.height(48.dp))
                Box(modifier = Modifier.height(40.dp).padding(start = 40.dp)) {
                    Image(painter = painterResource("images/stockary_logo.png"), null)
                }
                Spacer(Modifier.height(48.dp))
                repeat(NavItems.size) {
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        label = { Text(NavItems[it]) },
                        selected = selectedItem.value == it,
                        onClick = {
                            selectedItem.value = it
                        },
                        modifier = Modifier.testTag("nav_${NavItems[it]}")
                            .padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFD6E2F8), unselectedContainerColor = Color.Transparent
                        )
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = {

                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFFD6E2F8),
                        unselectedContainerColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(56.dp))
            },
            modifier = Modifier.padding(innerPaddings),
            drawerContainerColor = Color(0xFFF0F6FF),
            drawerContentColor = contentColorFor(Color(0xFFF0F6FF))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(48.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(end = 50.dp)
                ) {
                    Box(
                        modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFFE2E3FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Shaad & Co", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(48.dp))
                Box(modifier = Modifier.fillMaxSize()) {
                    when (selectedItem.value) {
                        0 -> Overview()
                        1 -> Product()
                        2 -> Customer()
                        3 -> Orders()
                        4 -> NewProduct()
                        5 -> NewCategory()
                        6 -> Categories()
                    }
                }
            }
        }
    }
}
