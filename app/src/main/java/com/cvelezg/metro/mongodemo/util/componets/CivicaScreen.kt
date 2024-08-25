package com.cvelezg.metro.mongodemo.util.componets

import android.content.res.Configuration
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.cvelezg.metro.mongodemo.R
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MetroLoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.medellin), // Reemplaza con tu imagen
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        MetroAnimatedLine(modifier = Modifier.size(200.dp))
    }
}

@Composable
fun MetroAnimatedLine(modifier: Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val position by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Canvas(modifier = modifier) {
        val circleRadius = size.minDimension / 5
        drawCircle(
            color = Color(0xFFFFFFFF), // Green color for the metro track
            radius = circleRadius,
            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        )

        val angleInRadians = Math.toRadians(position * 360.0)
        val metroWidth = 80.dp.toPx() // Width of the metro
        val metroHeight = 20.dp.toPx() // Height of the metro
        val metroRadius = circleRadius - metroHeight / 2
        val metroX = center.x + metroRadius * cos(angleInRadians).toFloat()
        val metroY = center.y + metroRadius * sin(angleInRadians).toFloat()

        val path = Path().apply {
            arcTo(
                rect = Rect(
                    left = center.x - circleRadius,
                    top = center.y - circleRadius,
                    right = center.x + circleRadius,
                    bottom = center.y + circleRadius
                ),
                startAngleDegrees = position * 360f,
                sweepAngleDegrees = 30f, // Adjust if needed
                forceMoveTo = false
            )
        }

        drawPath(
            path = path,
            brush = Brush.linearGradient(
                colors = listOf(Color.DarkGray, Color.Gray),
                start = Offset.Zero,
                end = Offset(metroWidth, metroHeight)
            ),
            style = Stroke(width = metroHeight)
        )
    }
}
