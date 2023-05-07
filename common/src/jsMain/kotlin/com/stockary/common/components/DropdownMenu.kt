package com.stockary.common.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SignOutMenu(
    expanded: Boolean = false,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(onClick = onLogout, text = {
            Text("Logout")
        })
    }
}


@Composable
fun CustomDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun CustomDropdownMenuItems(
    text: @Composable () -> Unit,
    onClick: () -> Unit
) {
    DropdownMenuItem(text = {
        text()
    }, onClick = onClick)
}