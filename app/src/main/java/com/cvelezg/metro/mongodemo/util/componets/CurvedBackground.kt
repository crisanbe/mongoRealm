package com.cvelezg.metro.mongodemo.util.componets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp

@Composable
fun CurvedBackground() {
    Canvas(
        modifier = Modifier.fillMaxSize(),
        onDraw = {
            // Define the height percentages
            val greenHeight = size.height * 0.3f
            val whiteHeight = size.height * 0.7f

            // Draw the green section (top 30%)
            drawRect(
                color = Color(0xFF558B2F),
                topLeft = Offset(0f, 0f),
                size = size.copy(height = greenHeight)
            )

            // Draw the white section (bottom 70%)
            drawRect(
                color =  Color(0xFF558B2F),
                topLeft = Offset(0f, greenHeight),
                size = size.copy(height = whiteHeight)
            )

            // Draw the curved divider
            val path = Path().apply {
                moveTo(0f, greenHeight)
                quadraticBezierTo(
                    size.width / 2, greenHeight + 100.dp.toPx(), // Curve height adjustment pointing downwards
                    size.width, greenHeight
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = Color(0xFFF7F7F7) // Green color for the curved divider
            )
        }
    )
}
