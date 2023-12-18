package com.raul.stockmarket.presentation.company_info

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raul.stockmarket.domain.model.IntradayInfo
import java.time.LocalDateTime
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.random.Random


fun getTime(substractHours: Int): LocalDateTime {
    val isPositive = substractHours > 0
    val now = LocalDateTime.now()
    return now.minusHours(substractHours.toLong())
}

fun createIntradayInfo(substractHours: Int, close: Int): IntradayInfo {
    return IntradayInfo(
        date = getTime(substractHours),
        close = close.toDouble()
    )
}


val a = createIntradayInfo(1, 100)
val b = createIntradayInfo(2, 98)
val c = createIntradayInfo(3, 105)
val d = createIntradayInfo(0, 120)
val e = createIntradayInfo(-1, 122)
val f = createIntradayInfo(-2, 70)
val g = createIntradayInfo(-3, 85)
val h = createIntradayInfo(-4, 88)
val i = createIntradayInfo(-5, 201)
val j = createIntradayInfo(-6, 180)
val k = createIntradayInfo(-7, 130)
val l = createIntradayInfo(-8, 150)
val m = createIntradayInfo(-9, 145)
val n = createIntradayInfo(-10, 155)
val q = createIntradayInfo(-11, 300)


val testList = listOf<IntradayInfo>(a, b, c, d, e, f, g, h, i, j, k, l, m, n)

fun createRandomList(size: Int, max: Int, min: Int): List<IntradayInfo> {
    val list = mutableListOf<IntradayInfo>()
    val now = LocalDateTime.now()
    (0..size).forEach {
        val stockValue = Random.nextDouble(min.toDouble(), max.toDouble())
        val date = now.plusHours(it.toLong())
        val stockInfo = IntradayInfo(
            date = date,
            close = stockValue
        )
        list.add(stockInfo)
    }

    return list
}


@Composable
fun StockChart(
    infos: List<IntradayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Color.Green,

    ) {
    val spacing = 100f
    val transparentGraphColor = remember {
        graphColor.copy(0.5f)
    }

    val upperValue = remember {
        (infos.maxOfOrNull { it.close }?.plus(1)?.roundToInt() ?: 0)

    }

    val lowerValue = remember {
        (infos.minOfOrNull { it.close }?.toInt()) ?: 0
    }

    val density = LocalDensity.current

    val textPaint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }

        }
    }

    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / infos.size
        (0 until infos.size - 1 step 2).forEach { i ->
            val info = infos[i]
            val hour = info.date.hour
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    spacing + i * spacePerHour,
                    size.height - 5,
                    textPaint
                )
            }
        }
        val priceStep = (upperValue - lowerValue) / 5f
        (0..4).forEach { i ->
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    textPaint
                )
            }
        }
        var lastX = 0f
        val strokePath = Path().apply {
            val height = size.height
            for(i in infos.indices) {
                val info = infos[i]
                val nextInfo = infos.getOrNull(i + 1) ?: infos.last()
                val leftRatio = (info.close - lowerValue) / (upperValue - lowerValue)
                val rightRatio = (nextInfo.close - lowerValue) / (upperValue - lowerValue)

                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRatio * height).toFloat()
                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()
                if(i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                quadraticBezierTo(
                    x1, y1, lastX, (y1 + y2) / 2f
                )
            }
        }
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath()
            .apply {
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close()
            }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}