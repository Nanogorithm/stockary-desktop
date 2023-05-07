package com.stockary.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.stockary.common.components.CustomDropdownMenu
import com.stockary.common.components.CustomDropdownMenuItems


actual fun getPlatformName(): String {
    return "Browser"
}

@Composable
actual fun SignOutMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    SignOutMenu(expanded = expanded, onDismiss = onDismiss, onLogout = onLogout)
}


@Composable
actual fun CustomDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    CustomDropdownMenu(expanded = expanded, onDismiss = onDismiss, modifier = modifier) {
        content()
    }
}

@Composable
actual fun CustomDropdownMenuItem(text: @Composable () -> Unit, onClick: () -> Unit) {
    CustomDropdownMenuItems(text = text, onClick = onClick)
}