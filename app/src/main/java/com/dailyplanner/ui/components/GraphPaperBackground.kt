package com.dailyplanner.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GraphPaperBackground(
    lineColor: Color = Color(0xFFD4C9B0).copy(alpha = 0.5f),
    cellSize: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cellPx = cellSize.toPx()
        val cols = (size.width / cellPx).toInt() + 1
        val rows = (size.height / cellPx).toInt() + 1

        for (c in 0..cols) {
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(c * cellPx, 0f),
                end = androidx.compose.ui.geometry.Offset(c * cellPx, size.height),
                strokeWidth = 1f
            )
        }
        for (r in 0..rows) {
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(0f, r * cellPx),
                end = androidx.compose.ui.geometry.Offset(size.width, r * cellPx),
                strokeWidth = 1f
            )
        }
    }
}
