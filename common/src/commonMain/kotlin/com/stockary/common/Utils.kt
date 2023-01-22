package com.stockary.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp

//import io.appwrite.Client

val currencySymbol = "৳"

fun Float.toCurrencyFormat() = "${currencySymbol}${this}"

//val client = Client().setEndpoint("http://stockary.helloanwar.com/v1") // Your API Endpoint
//    .setProject("63a3f8a029aee18d7448") // Your project ID
//    .setKey(
//        "e30afefba1764c335670704bd1747be56901c394ec43c06ceefc29a4841ba9c370f7d0a4ca519a1bcde5e6052734741490a51c4d3f40bf71ee9f5a2df2df0e7b2b000ff290e07020799f2d76c1d120d9280c08771c46926246a5fa72561f718fb9d05d00027a0d371867d5c94b65459273f5c3ca2705952c55d45868a0a9307b"
//    ).setSelfSigned(true)

sealed interface SupabaseResource<out R> {
    class Success<out T>(val data: T) : SupabaseResource<T>
    class Error<out T>(val exception: Throwable, val data: T? = null) : SupabaseResource<T>
    object Loading : SupabaseResource<Nothing>
    object Idle : SupabaseResource<Nothing>
}

fun Modifier.dashedBorder(width: Dp, radius: Dp, color: Color) =
    drawBehind {
        drawIntoCanvas {
            val paint = Paint()
                .apply {
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