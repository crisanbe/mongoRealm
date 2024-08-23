package com.cvelezg.metro.mongodemo.util.componets

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cvelezg.metro.mongodemo.R
import com.cvelezg.metro.mongodemo.model.LocationData
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport

@Composable
fun MapScreen(
    context: Context,
    location: LocationData? = null,
    modifier: Modifier = Modifier
) {
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapboxMap by remember { mutableStateOf<MapboxMap?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    mapboxMap = this.getMapboxMap()
                    mapboxMap!!.loadStyleUri(Style.MAPBOX_STREETS)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Button(
            onClick = {
                location?.let { loc ->
                    mapboxMap?.setCamera(
                        CameraOptions.Builder()
                            .center(Point.fromLngLat(loc.longitude, loc.latitude))
                            .zoom(15.0)
                            .build()
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .size(80.dp)
        ) {
            Text("Seguir")
        }
    }

    LaunchedEffect(mapboxMap) {
        mapboxMap?.let { map ->
            val locationComponent = mapView?.location ?: return@let
            locationComponent.updateSettings {
                enabled = true
                pulsingEnabled = true
            }
            locationComponent.locationPuck = LocationPuck2D(
                topImage = ImageHolder.from(org.osmdroid.library.R.drawable.next),
                bearingImage = ImageHolder.from(org.osmdroid.library.R.drawable.osm_ic_center_map),
                shadowImage = ImageHolder.from(org.osmdroid.library.R.drawable.center)
            )
        }
    }

    LaunchedEffect(mapboxMap) {
        mapboxMap?.let { map ->
            val viewportPlugin = mapView?.viewport ?: return@let
            val followPuckViewportState = viewportPlugin.makeFollowPuckViewportState(
                FollowPuckViewportStateOptions.Builder()
                    .bearing(FollowPuckViewportStateBearing.Constant(0.0))
                    .padding(EdgeInsets(200.0 * context.resources.displayMetrics.density, 0.0, 0.0, 0.0))
                    .build()
            )
            viewportPlugin.transitionTo(followPuckViewportState)
        }
    }

    LaunchedEffect(location) {
        location?.let { loc ->
            mapboxMap?.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(loc.longitude, loc.latitude))
                    .zoom(15.0)
                    .build()
            )
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            location?.let { loc ->
                mapboxMap?.setCamera(
                    CameraOptions.Builder()
                        .center(Point.fromLngLat(loc.longitude, loc.latitude))
                        .zoom(15.0)
                        .build()
                )
            }
            kotlinx.coroutines.delay(1000) // Update every second
        }
    }
}
