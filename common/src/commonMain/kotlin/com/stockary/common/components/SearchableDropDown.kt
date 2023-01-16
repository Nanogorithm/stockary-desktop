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
private fun FileDialog(
    parent: Frame? = null, onCloseRequest: (result: String?) -> Unit
) = AwtWindow(
    create = {
        object : FileDialog(parent, "Choose a file", LOAD) {
            override fun setVisible(value: Boolean) {
                super.setVisible(value)
                if (value) {
                    val path: String = directory + file
                    onCloseRequest(path)
                }
            }
        }
    }, dispose = FileDialog::dispose
)