package com.stockary.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

expect fun getPlatformName(): String

@Composable
expect fun SignOutMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
)

@Composable
expect fun CustomDropdown(
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier,
    content: @Composable () -> Unit
)
@Composable
expect fun CustomDropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit
)