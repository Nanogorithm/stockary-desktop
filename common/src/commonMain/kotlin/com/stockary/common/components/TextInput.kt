@file:OptIn(ExperimentalMaterial3Api::class)

package com.stockary.common.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.AwtWindow
import com.stockary.common.SupabaseResource
import com.stockary.common.currencySymbol
import com.stockary.common.dashedBorder
import com.stockary.common.form_builder.TextFieldState
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.lazyPainterResource
import java.awt.FileDialog
import java.awt.Frame
import java.io.File


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
fun FileChooser(
    state: TextFieldState,
    modifier: Modifier = Modifier.fillMaxWidth().height(90.dp),
    uploadResponse: SupabaseResource<String>
) {
    var isFileChooserOpen by remember { mutableStateOf(false) }
    Column {
        Box(
            modifier = modifier.clip(RoundedCornerShape(10.dp)).background(color = Color.White)
                .dashedBorder(1.dp, 10.dp, Color(0xFF959191)).clickable {
                    isFileChooserOpen = true
                }, contentAlignment = Alignment.Center
        ) {
            if (state.value.isNotBlank()) {
                println("photo -> ${state.value}")
                val painterResource: Resource<Painter> =
                    lazyPainterResource(data = if (state.value.contains("https://")) state.value else File(state.value))
                KamelImage(
                    resource = painterResource,
                    contentDescription = "Product photo",
                    onLoading = { progress -> CircularProgressIndicator(progress) },
                    onFailure = { exception ->

                    },
                    animationSpec = tween(),
                )
            } else {
                Icon(Icons.Default.AddAPhoto, null)
            }
            when (uploadResponse) {
                is SupabaseResource.Error -> {
                    Text(uploadResponse.data.toString())
                }

                SupabaseResource.Idle -> {

                }

                SupabaseResource.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is SupabaseResource.Success -> {
                    Icon(
                        Icons.Default.CheckCircle,
                        "",
                        modifier = Modifier.align(alignment = Alignment.BottomEnd),
                        tint = Color.Green
                    )
                }
            }
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

        if (isFileChooserOpen) {
            FileDialog {
                isFileChooserOpen = false
                it?.let {
                    //process to upload
                    state.change(it)
                }
            }
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