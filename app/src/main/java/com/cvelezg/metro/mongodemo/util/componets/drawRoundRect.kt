package com.cvelezg.metro.mongodemo.util.componets

import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke

fun DrawScope.drawRoundRect(
    color: Color,
    topLeft: Offset = Offset.Zero,
    size: Size = Size(this.size.width - topLeft.x, this.size.height - topLeft.y),
    cornerRadius: CornerRadius = CornerRadius.Zero,
    alpha: Float = 1.0f,
    style: DrawStyle = Fill
) {
    drawContext.canvas.drawRoundRect(
        topLeft.x,
        topLeft.y,
        topLeft.x + size.width,
        topLeft.y + size.height,
        cornerRadius.x,
        cornerRadius.y,
        Paint().apply {
            this.color = color
            this.alpha = (alpha * 200).toInt().toFloat()
            this.style = style.toPaintStyle()
        }
    )
}


fun DrawStyle.toPaintStyle(): PaintingStyle {
    return when (this) {
        is Fill -> PaintingStyle.Fill
        is Stroke -> PaintingStyle.Stroke
        else -> PaintingStyle.Fill
    }
}