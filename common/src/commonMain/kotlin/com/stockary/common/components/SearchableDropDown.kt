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
import androidx.compose.ui.window.AwtWindow
import com.stockary.common.repository.category.model.Category
import com.stockary.common.repository.product.model.UnitType
import java.awt.FileDialog
import java.awt.Frame

@Composable
fun SearchableDropDown(
    items: List<Category>, modifier: Modifier = Modifier, label: String = "Category", onSelected: (Category) -> Unit
) {
    var selected by remember { mutableStateOf(-1) }
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
                Text(if (selected < 0) "select ${label.lowercase()}" else items[selected].title)
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
                    selected = index
                    onSelected(items[index])
                    showPop = false
                    search = ""
                }) { Text(text = element.title) }
            }
        }
    }
}

@Composable
fun SelectUnitType(
    items: List<UnitType>,
    modifier: Modifier = Modifier,
    onSelected: (UnitType) -> Unit
) {
    var selected by remember { mutableStateOf(-1) }
    var showPop by remember { mutableStateOf(false) }
    var search by remember { mutableStateOf("") }

    Box(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp)
                .background(Color.White)
                .clickable {
                    showPop = true
                }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).wrapContentHeight()
            ) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(if (selected < 0) "select Unit type" else items[selected].name)
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
                    selected = index
                    onSelected(items[index])
                    showPop = false
                    search = ""
                }) { Text(text = element.name) }
            }
        }
    }
}