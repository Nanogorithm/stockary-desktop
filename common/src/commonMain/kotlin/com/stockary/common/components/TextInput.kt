package com.stockary.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.stockary.common.currencySymbol
import com.stockary.common.form_builder.TextFieldState


@Composable
fun TextInput(
    label: String, state: TextFieldState, placeHolder: String, maxLines: Int = 1, modifier: Modifier = Modifier
) {
    Column {
        TextField(
            value = state.value,
            onValueChange = {
                state.change(it)
            },
            placeholder = { Text(placeHolder) },
            label = { Text(label) },
            modifier = modifier,
            singleLine = maxLines == 1,
            maxLines = maxLines,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                textColor = contentColorFor(Color.White),
                unfocusedIndicatorColor = Color.Transparent,
                placeholderColor = Color(0xFF676767)
            ),
            isError = state.hasError
        )

        if (state.hasError) {
            Text(
                text = state.errorMessage,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                style = MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.error
                )
            )
        }
    }
}

@Composable
fun CurrencyInput(
    label: String, state: TextFieldState, placeHolder: String, modifier: Modifier = Modifier
) {
    Column {
        TextField(value = state.value, onValueChange = {
            state.change(it)
        }, placeholder = {
            Text(placeHolder)
        }, singleLine = true, colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.White,
            textColor = contentColorFor(Color.White),
            unfocusedIndicatorColor = Color.Transparent,
            placeholderColor = Color(0xFF676767)
        ), leadingIcon = {
            Text(
                currencySymbol, color = Color(0xFF1F1F1F), fontWeight = FontWeight.W500
            )
        }, modifier = modifier, label = {
            Text(label)
        }, isError = state.hasError
        )

        if (state.hasError) {
            Text(
                text = state.errorMessage,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                style = MaterialTheme.typography.caption.copy(
                    color = MaterialTheme.colors.error
                )
            )
        }
    }
}