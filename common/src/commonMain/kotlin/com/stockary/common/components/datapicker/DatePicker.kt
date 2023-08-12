package com.stockary.common.components.datapicker

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerUI(
    label: String,
    onDismissRequest: (Date?) -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier.fillMaxWidth().wrapContentHeight()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 10.dp, horizontal = 5.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            val chosenYear = remember { mutableStateOf(currentYear) }
            val chosenMonth = remember { mutableStateOf(currentMonth) }
            val chosenDay = remember { mutableStateOf(currentDay) }

            DateSelectionSection(
                onYearChosen = { chosenYear.value = it.toInt() },
                onMonthChosen = { chosenMonth.value = monthsNames.indexOf(it) },
                onDayChosen = { chosenDay.value = it.toInt() },
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .padding(horizontal = 10.dp),
                    onClick = {
                        onDismissRequest(null)
                    }
                ) {
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
                Button(
                    shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentHeight()
                        .padding(horizontal = 10.dp),
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.time = Date()
                        calendar[Calendar.YEAR] = chosenYear.value
                        calendar[Calendar.MONTH] = chosenMonth.value
                        calendar[Calendar.DAY_OF_MONTH] = chosenDay.value
                        calendar[Calendar.HOUR_OF_DAY] = 0
                        calendar[Calendar.MINUTE] = 0
                        calendar[Calendar.SECOND] = 0
                        calendar[Calendar.MILLISECOND] = 0

                        onDismissRequest(calendar.time)
                    }
                ) {
                    Text(
                        text = "Confirm",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }


        }
    }
}

@Composable
fun DateSelectionSection(
    onYearChosen: (String) -> Unit,
    onMonthChosen: (String) -> Unit,
    onDayChosen: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .height(164.dp)
    ) {
        InfiniteItemsPicker(
            items = days,
            firstIndex = Int.MAX_VALUE / 2 + (currentDay - 2),
            onItemSelected = onDayChosen
        )

        InfiniteItemsPicker(
            items = monthsNames,
            firstIndex = Int.MAX_VALUE / 2 - 4 + currentMonth,
            onItemSelected = onMonthChosen
        )

        InfiniteItemsPicker(
            items = years,
            firstIndex = Int.MAX_VALUE / 2 + (currentYear - 1967),
            onItemSelected = onYearChosen
        )
    }
}

@Composable
fun InfiniteItemsPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
    firstIndex: Int,
    onItemSelected: (String) -> Unit,
) {

    val listState = rememberLazyListState(firstIndex)
    val currentValue = remember { mutableStateOf("") }

    LaunchedEffect(key1 = !listState.isScrollInProgress) {
        onItemSelected(currentValue.value)
        listState.animateScrollToItem(index = listState.firstVisibleItemIndex)
    }

    Box(modifier = Modifier.height(164.dp)) {
        Column {
            Icon(Icons.Default.ArrowDropUp, null)
            LazyColumn(
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                items(count = Int.MAX_VALUE) {
                    val index = it % items.size
                    if (it == listState.firstVisibleItemIndex + 1) {
                        currentValue.value = items[index]
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = items[index],
                        modifier = Modifier.alpha(if (it == listState.firstVisibleItemIndex + 1) 1f else 0.3f),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
            Icon(Icons.Default.ArrowDropDown, null)
        }
    }
}

val currentYear = Calendar.getInstance().get(Calendar.YEAR)
val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
val currentMonth = Calendar.getInstance().get(Calendar.MONTH)

val years = (1950..2050).map { it.toString() }
val monthsNumber = (1..12).map { it.toString() }
val days = (1..31).map { it.toString() }
val monthsNames = listOf(
    "Jan",
    "Feb",
    "Mar",
    "Apr",
    "May",
    "Jun",
    "Jul",
    "Aug",
    "Sep",
    "Oct",
    "Nov",
    "Dec"
)