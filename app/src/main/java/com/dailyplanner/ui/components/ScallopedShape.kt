package com.dailyplanner.ui.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class ScallopedShape(
    private val scallops: Int = 8,
    private val scallopDepth: Float = 0.04f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path()
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rx = size.width / 2f
        val ry = size.height / 2f

        val totalPoints = scallops * 2
        val angleStep = (2 * PI / totalPoints).toFloat()

        for (i in 0 until totalPoints) {
            val angle = i * angleStep - PI.toFloat() / 2
            val depth = if (i % 2 == 0) 1f else (1f - scallopDepth)
            val x = cx + rx * depth * cos(angle)
            val y = cy + ry * depth * sin(angle)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return Outline.Generic(path)
    }
}

class BlobShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            moveTo(w * 0.05f, h * 0.35f)
            cubicTo(w * -0.05f, h * 0.1f, w * 0.2f, h * -0.05f, w * 0.5f, h * 0.03f)
            cubicTo(w * 0.75f, h * -0.02f, w * 1.05f, h * 0.1f, w * 0.97f, h * 0.4f)
            cubicTo(w * 1.05f, h * 0.65f, w * 0.9f, h * 1.05f, w * 0.6f, h * 0.97f)
            cubicTo(w * 0.35f, h * 1.05f, w * -0.02f, h * 0.95f, w * 0.05f, h * 0.7f)
            cubicTo(w * -0.02f, h * 0.55f, w * -0.02f, h * 0.45f, w * 0.05f, h * 0.35f)
            close()
        }
        return Outline.Generic(path)
    }
}
