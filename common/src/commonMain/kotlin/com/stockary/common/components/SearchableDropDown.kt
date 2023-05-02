package com.stockary.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.stockary.common.form_builder.ChoiceState
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.customer.model.Role
import com.stockary.common.repository.product.model.UnitType

@Composable
fun SearchableDropDown(
    items: List<Category>, modifier: Modifier = Modifier, label: String = "Category", state: ChoiceState
) {
    var showPop by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable {
                    showPop = true
                }, horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).wrapContentHeight()
            ) {
                Text(label, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (state.value == "") "Select ${label.lowercase()}" else items.firstOrNull { it.title == state.value }?.title
                        ?: "Select Category"
                )
            }
            Icon(if (showPop) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
        }

        DropdownMenu(
            expanded = showPop, onDismissRequest = { showPop = false }, modifier = Modifier.size(250.dp, 350.dp)
        ) {
            TextField(value = search, onValueChange = { search = it }, placeholder = { Text("Search") })
            items.filter {
                it.title.contains(search)
            }.forEachIndexed { index, element ->
                DropdownMenuItem(onClick = {
                    state.change(element.title)
                    showPop = false
                    search = ""
                }) { Text(text = element.title) }
            }
        }
    }
}

@Composable
fun SelectUnitType(
    items: List<UnitType>, modifier: Modifier = Modifier, state: ChoiceState
) {
    var showPop by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.White).clickable {
                showPop = true
            }.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).wrapContentHeight()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (state.value == "") "Select Unit type" else items.firstOrNull { it.name == state.value }?.name
                        ?: "Select Unit type"
                )
            }
            Icon(if (showPop) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
        }

        DropdownMenu(
            expanded = showPop, onDismissRequest = { showPop = false }, modifier = Modifier.size(250.dp, 350.dp)
        ) {
            TextField(value = search, onValueChange = { search = it }, placeholder = { Text("Search") })
            items.filter {
                it.name.contains(search)
            }.forEachIndexed { index, element ->
                DropdownMenuItem(onClick = {
                    state.change(element.name)
                    showPop = false
                    search = ""
                }) { Text(text = element.name) }
            }
        }
    }
}

@Composable
fun SelectCustomerType(
    items: List<Role>, modifier: Modifier = Modifier, state: ChoiceState
) {
    var showPop by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(Color.White).clickable {
                showPop = true
            }.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).wrapContentHeight()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (state.value == "") "Select Customer type" else items.firstOrNull { it.slug.toString() == state.value }?.title
                        ?: "Select Customer type"
                )
            }
            Icon(if (showPop) Icons.Default.ExpandLess else Icons.Default.ExpandMore, null)
        }

        DropdownMenu(
            expanded = showPop, onDismissRequest = { showPop = false }, modifier = Modifier.size(250.dp, 350.dp)
        ) {
            TextField(value = search, onValueChange = { search = it }, placeholder = { Text("Search") })
            items.filter {
                it.title?.contains(search) == true
            }.forEachIndexed { index, element ->
                DropdownMenuItem(onClick = {
                    state.change(element.slug ?: "")
                    showPop = false
                    search = ""
                }) { Text(text = element.title ?: "") }
            }
        }
    }
}