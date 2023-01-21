package com.stockary.common.form_builder

fun String.isNumeric(): Boolean {
    return this.toIntOrNull()?.let { true } ?: false
}