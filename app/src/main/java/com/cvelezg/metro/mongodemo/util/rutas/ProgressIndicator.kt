package com.cvelezg.metro.mongodemo.util.rutas

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.navigation.base.trip.model.RouteProgress

@SuppressLint("DefaultLocale")
@Composable
fun ProgressIndicator(progress: RouteProgress?) {
    val remainingDistance = (progress?.distanceRemaining ?: 0.0) as Double
    val formattedDistance = String.format("%.2f km", remainingDistance / 1000.0)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column {
            LinearProgressIndicator(
                progress = (1 - remainingDistance / 10000.0).toFloat(), // Normalizar a 0-1
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Distance remaining: $formattedDistance",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
