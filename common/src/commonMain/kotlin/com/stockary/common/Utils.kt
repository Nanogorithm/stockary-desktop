package com.stockary.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.Dp
import java.text.Normalizer
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

val currencySymbol = "৳"
val storagePrefix = "https://nfwwajxqeilqdkvwfojz.supabase.co/storage/v1/object/public/"

fun Float.toCurrencyFormat() = "${currencySymbol}${this}"
fun Double.toCurrencyFormat() = "${currencySymbol}${this}"

fun Float.removeEmptyFraction(): String {
    try {
        val roundTo = String.format("%.2f", this)
        println("number => $roundTo")
        if (roundTo.split(".").last().toInt() > 0) {
            return roundTo
        }
    } catch (e: Exception) {
        println("number => $this")
        e.printStackTrace()
    }

    return this.toInt().toString()
}

fun Double.removeEmptyFraction(): String {
    try {
        val roundTo = String.format("%.2f", this)
        if (roundTo.split(".").last().toInt() > 0) {
            return roundTo
        }
    } catch (e: Exception) {
        println("number => $this")
        e.printStackTrace()
    }

    return this.toInt().toString()
}

fun String.slugify(replacement: String = "_") =
    Normalizer.normalize(this, Normalizer.Form.NFD).replace("[^\\p{ASCII}]".toRegex(), "")
        .replace("[^a-zA-Z0-9\\s]+".toRegex(), "").trim().replace("\\s+".toRegex(), replacement)
        .lowercase(Locale.getDefault())


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


fun yesterday(): Date {
    val now: Instant = today().toInstant()
    return Date.from(now.minus(1, ChronoUnit.DAYS)).startOfDay()
}

fun last7Days(): Date {
    val now: Instant = today().toInstant()
    return Date.from(now.minus(7, ChronoUnit.DAYS)).startOfDay()
}

fun today(): Date {
    return Date().startOfDay()
}

fun Date.startOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar[Calendar.HOUR_OF_DAY] = 0
    calendar[Calendar.MINUTE] = 0
    calendar[Calendar.SECOND] = 0
    calendar[Calendar.MILLISECOND] = 0
    return calendar.time
}

fun Date.endOfDay(): Date {
    val calendar = Calendar.getInstance()
    calendar.time = this
    calendar[Calendar.HOUR_OF_DAY] = 23
    calendar[Calendar.MINUTE] = 59
    calendar[Calendar.SECOND] = 59
    calendar[Calendar.MILLISECOND] = 999
    return calendar.time
}