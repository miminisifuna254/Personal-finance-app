package com.dailyplanner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StarburstIcon(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    color: Color = Color(0xFFC9B99A),
    rays: Int = 12
) {
    Canvas(modifier = modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val outerR = this.size.minDimension / 2f
        val innerR = outerR * 0.55f

        val path = Path()
        val points = rays * 2
        for (i in 0 until points) {
            val angle = (i * PI / rays - PI / 2).toFloat()
            val r = if (i % 2 == 0) outerR else innerR
            val x = cx + r * cos(angle)
            val y = cy + r * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        drawPath(path, color)
    }
}

@Composable
fun LeafDecor(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    color: Color = Color(0xFF7A9E7E).copy(alpha = 0.5f)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val path = Path().apply {
            moveTo(w * 0.5f, 0f)
            cubicTo(w * 0.9f, h * 0.1f, w * 1.0f, h * 0.6f, w * 0.5f, h)
            cubicTo(w * 0.0f, h * 0.6f, w * 0.1f, h * 0.1f, w * 0.5f, 0f)
            close()
        }
        drawPath(path, color)
    }
}

@Composable
fun PetalDecor(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    color: Color = Color(0xFFC9B99A).copy(alpha = 0.6f),
    petals: Int = 5
) {
    Canvas(modifier = modifier.size(size)) {
        val cx = this.size.width / 2f
        val cy = this.size.height / 2f
        val r = this.size.minDimension / 2f * 0.85f

        for (i in 0 until petals) {
            val angle = (i * 360f / petals)
            rotate(degrees = angle, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
                val path = Path().apply {
                    moveTo(cx, cy)
                    cubicTo(cx + r * 0.5f, cy - r * 0.2f, cx + r * 0.5f, cy - r, cx, cy - r)
                    cubicTo(cx - r * 0.5f, cy - r, cx - r * 0.5f, cy - r * 0.2f, cx, cy)
                    close()
                }
                drawPath(path, color)
            }
        }
        drawCircle(color = color, radius = r * 0.2f, center = androidx.compose.ui.geometry.Offset(cx, cy))
    }
}
