package com.helloanwar.common.ui.components.tableview

import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource


@Composable
inline fun <reified T : Any> TableView(
    currentItem: MutableState<T?>,
    content: MutableState<List<T>>,
    indexColumn: Boolean = false,
    indexColWidth: Dp = 30.dp,
    noinline onRowSelection: (T) -> Unit,
    noinline actions: @Composable (RowScope.(T) -> Unit)? = null
) {
    val fields = T::class.members.filter {
        it.annotations.any { a -> a is TableHeader }
    }.sortedBy {
        val header = getTableHeader(it.annotations)
        header.columnIndex
    }

    var search by remember { mutableStateOf("") }

    val tableContent = remember(content.value) { mutableStateOf(content.value) }
    val onContentUpdate: (String) -> Unit = {
        search = it
        val filteredList = content.value.filter {
            val values = fields.map { kc ->
                val value = kc.call(it)
                if (value == null) {
                    ""
                } else {
                    "$value"
                }
            }
            val match = values.any { f -> f.contains(search, true) }
            match
        }
        tableContent.value = filteredList
    }

    Column(
        Modifier.fillMaxWidth()
    ) {

        SearchField(search, onContentUpdate)

        val headerList = fields.flatMap { it.annotations }.filter { it is TableHeader && it.columnIndex >= 0 }
            .map { it as TableHeader }

        val stateMap = headerList.associateWith { mutableStateOf(SortingState.NONE) }
        val sortingStates = remember { mutableStateOf(stateMap) }

        val onSortingUpdate: (TableHeader, SortingState) -> Unit = { tableHeader, sortingState ->
            val sortedList: List<T> = when (sortingState) {
                SortingState.ASC -> {
                    tableContent.value.sortedBy { t ->
                        sort(t, tableHeader)
                    }
                }

                SortingState.DESC -> {
                    tableContent.value.sortedByDescending { t ->
                        sort(t, tableHeader)
                    }
                }

                else -> {
                    tableContent.value
                }
            }

            tableContent.value = sortedList
        }

        TableHeader(
            indexColumn = indexColumn,
            indexColWidth = indexColWidth,
            headerList = headerList,
            sortingStates = sortingStates,
            onSortingUpdate = onSortingUpdate,
            actionsColumn = actions != null
        )
        TableContent(currentItem, tableContent, indexColumn, indexColWidth, onRowSelection, actions)
    }
}

@Composable
fun SearchField(
    search: String, onContentUpdate: (String) -> Unit
) {
    OutlinedTextField(value = search,
        onValueChange = {
            onContentUpdate(it)
        },
        label = { Text("Search ...") },
        modifier = Modifier.padding(10.dp).fillMaxWidth(),
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = {
                onContentUpdate("")
            }) {
                Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
            }
        })
}

@Composable
fun TableHeader(
    indexColumn: Boolean,
    actionsColumn: Boolean,
    indexColWidth: Dp,
    headerList: List<TableHeader>,
    sortingStates: MutableState<Map<TableHeader, MutableState<SortingState>>>,
    onSortingUpdate: (TableHeader, SortingState) -> Unit
) {
    Card(modifier = Modifier.padding(5.dp).fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(3.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (indexColumn) {
                Row(
                    modifier = Modifier.width(indexColWidth).height(30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold
                    )
                }
            }
            headerList.forEach {
                Row(
                    modifier = Modifier.fillMaxWidth().weight(1f).height(30.dp).clickable {
                        updateSortingStates(sortingStates, it)
                        onSortingUpdate(it, sortingStates.value[it]!!.value)
                    }, horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = it.headerText, style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold
                    )

                    when (sortingStates.value[it]!!.value) {
                        SortingState.DESC -> {
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "",
                                modifier = Modifier.width(15.dp)
                            )
                        }

                        SortingState.ASC -> {
                            Icon(
                                Icons.Filled.KeyboardArrowUp, contentDescription = "", modifier = Modifier.width(15.dp)
                            )
                        }

                        else -> {
                            // to be left empty
                        }
                    }
                }
            }
            if (actionsColumn) {
                Row(
                    modifier = Modifier.height(30.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Actions", style = MaterialTheme.typography.body1, fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
inline fun <reified T> TableContent(
    currentItem: MutableState<T?>,
    tableContent: MutableState<List<T>>,
    indexColumn: Boolean,
    indexColWidth: Dp,
    noinline onRowSelection: (T) -> Unit,
    noinline actions: @Composable (RowScope.(T) -> Unit)? = null
) {
    LazyColumn {
        println("size => ${tableContent.value.size}")
        itemsIndexed(items = tableContent.value) { index, item ->
//            val rowIndex = tableContent.value.indexOf(item) + 1
            TableRow(
                item = item,
                indexColumn = indexColumn,
                rowIndex = index + 1,
                indexColWidth = indexColWidth,
                onRowSelection = onRowSelection,
                selected = item == currentItem.value,
                actions = actions
            )
        }
    }
}

@Composable
inline fun <reified T> TableRow(
    item: T,
    indexColumn: Boolean,
    rowIndex: Int,
    indexColWidth: Dp,
    noinline onRowSelection: (T) -> Unit,
    selected: Boolean,
    noinline actions: @Composable (RowScope.(T) -> Unit)? = null
) {
    val color = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.background
    Card(
        modifier = Modifier.padding(3.dp).fillMaxWidth().clickable {
            onRowSelection(item)
        }, backgroundColor = color
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
        ) {
            if (indexColumn) {
                Row(
                    modifier = Modifier.width(indexColWidth),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "$rowIndex")
                }
            }

            val rowContent =
                item!!::class.members.filter { f -> f.annotations.any { a -> a is TableHeader && a.columnIndex >= 0 } }
                    .sortedBy { k ->
                        val header = getTableHeader(k.annotations)
                        header.columnIndex
                    }.map { t -> t.call(item) }
            rowContent.forEach { rc ->
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    if (rc != null) {
                        if (rc is ColumnType.Image) {
                            if (rc.src != null) {
                                Box(
                                    modifier = Modifier.padding(5.dp).size(42.dp).clip(RoundedCornerShape(6.dp))
                                        .padding(2.dp)
                                ) {
                                    val painterResource: Resource<Painter> = lazyPainterResource(
                                        data = rc.src
                                    )
                                    KamelImage(
                                        resource = painterResource,
                                        contentDescription = "Photo",
                                        onLoading = { progress -> CircularProgressIndicator(progress) },
                                        onFailure = { exception ->

                                        },
                                        animationSpec = tween(),
                                    )
                                }
                            } else {
                                Text(modifier = Modifier.padding(5.dp), text = "--")
                            }
                        } else {
                            Text(modifier = Modifier.padding(5.dp), text = "$rc")
                        }
                    } else {
                        Text(modifier = Modifier.padding(5.dp), text = "--")
                    }
                }
            }

            actions?.let {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically
                ) {
                    it(item)
                }
            }
        }
    }
}