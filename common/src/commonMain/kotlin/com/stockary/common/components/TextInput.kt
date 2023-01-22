package com.stockary.common.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import com.stockary.common.currencySymbol
import com.stockary.common.dashedBorder
import com.stockary.common.form_builder.TextFieldState
import java.awt.FileDialog
import java.awt.Frame


@Composable
fun TextInput(
    label: String, state: TextFieldState, placeHolder: String, maxLines: Int = 1, modifier: Modifier = Modifier
) {
    Column {
        TextField(value = state.value,
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
fun FileChooser(
    state: TextFieldState, modifier: Modifier = Modifier.fillMaxWidth().height(90.dp)
) {
    Column {
        Box(modifier = modifier.dashedBorder(1.dp, 10.dp, Color(0xFF959191)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.AddAPhoto, null)
        }
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
fun FileDialog(
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