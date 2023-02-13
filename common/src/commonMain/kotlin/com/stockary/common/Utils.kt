package com.stockary.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import java.io.OutputStream

val currencySymbol = "৳"
val storagePrefix = "https://nfwwajxqeilqdkvwfojz.supabase.co/storage/v1/object/public/"

fun Float.toCurrencyFormat() = "${currencySymbol}${this}"
fun Double.toCurrencyFormat() = "${currencySymbol}${this}"

sealed interface SupabaseResource<out R> {
    class Success<out T>(val data: T) : SupabaseResource<T>
    class Error<out T>(val exception: Throwable, val data: T? = null) : SupabaseResource<T>
    object Loading : SupabaseResource<Nothing>
    object Idle : SupabaseResource<Nothing>
}

fun Modifier.dashedBorder(width: Dp, radius: Dp, color: Color) = drawBehind {
    drawIntoCanvas {
        val paint = Paint().apply {
                strokeWidth = width.toPx()
                this.color = color
                style = PaintingStyle.Stroke
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            }
        it.drawRoundRect(
            width.toPx(),
            width.toPx(),
            size.width - width.toPx(),
            size.height - width.toPx(),
            radius.toPx(),
            radius.toPx(),
            paint
        )
    }
}