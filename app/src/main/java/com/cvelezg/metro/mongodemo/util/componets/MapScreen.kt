package com.cvelezg.metro.mongodemo.util.componets

import com.mapbox.maps.extension.style.layers.getLayerAs
import com.mapbox.maps.extension.style.sources.getSourceAs
import android.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.cvelezg.metro.mongodemo.model.LocationData
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapView
import com.cvelezg.metro.mongodemo.R
import com.cvelezg.metro.mongodemo.util.rutas.ProgressIndicator
import com.mapbox.geojson.utils.PolylineUtils
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.LineLayer
import com.mapbox.maps.extension.style.layers.properties.generated.LineCap
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.easeTo
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import com.mapbox.maps.plugin.viewport.viewport
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.base.trip.model.RouteProgress
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.trip.session.RouteProgressObserver
import com.mapbox.navigation.ui.maps.route.RouteLayerConstants
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowApi
import com.mapbox.navigation.ui.maps.route.arrow.api.MapboxRouteArrowView
import com.mapbox.navigation.ui.maps.route.arrow.model.RouteArrowOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

@Composable
fun MapScreen(location: LocationData? = null) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapboxMap by remember { mutableStateOf(mapView?.getMapboxMap()) }
    var routeProgress by remember { mutableStateOf<RouteProgress?>(null) }
    var currentCameraLocation by remember { mutableStateOf<Point?>(null) }

    val routeArrow = remember { MapboxRouteArrowApi() }
    val routeArrowOptions = RouteArrowOptions.Builder(context)
        .withAboveLayerId(RouteLayerConstants.TOP_LEVEL_ROUTE_LINE_LAYER_ID)
        .build()
    val routeArrowView = remember { MapboxRouteArrowView(routeArrowOptions) }

    val mapboxNavigation = remember {
        MapboxNavigationProvider.create(NavigationOptions.Builder(context).build())
    }

    val routeProgressObserver = remember {
        object : RouteProgressObserver {
            override fun onRouteProgressChanged(progress: RouteProgress) {
                routeProgress = progress
                val updatedManeuverArrow = routeArrow.addUpcomingManeuverArrow(progress)
                routeArrowView.renderManeuverUpdate(mapboxMap?.style ?: return, updatedManeuverArrow)
            }
        }
    }

    DisposableEffect(Unit) {
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver)
        onDispose {
            mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    mapView = this
                    mapboxMap = this.getMapboxMap()
                    mapboxMap?.loadStyleUri(Style.MAPBOX_STREETS) { style ->
                        val locationComponent = mapView?.location ?: return@loadStyleUri
                        locationComponent.updateSettings {
                            enabled = true
                            pulsingEnabled = true
                        }
                        locationComponent.addOnIndicatorPositionChangedListener { location ->
                            val point = Point.fromLngLat(location.longitude(), location.latitude())
                            if (currentCameraLocation != point) {
                                currentCameraLocation = point
                                mapboxMap?.easeTo(
                                    CameraOptions.Builder()
                                        .center(point)
                                        .zoom(15.0)
                                        .build(),
                                    mapAnimationOptions { duration(1000L) }
                                )
                            }
                        }
                        fetchRouteAndRender(style)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        routeProgress?.let { progress ->
            ProgressIndicator(progress)
            Text(
                text = "Distance remaining: ${String.format("%.2f km", progress.distanceRemaining / 1000.0)}",
                modifier = Modifier.padding(16.dp).align(Alignment.TopCenter)
            )
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
                    .bearing(FollowPuckViewportStateBearing.SyncWithLocationPuck)
                    .padding(EdgeInsets(100.0 * context.resources.displayMetrics.density, 0.0, 0.0, 0.0))
                    .build()
            )
            viewportPlugin.transitionTo(followPuckViewportState)
        }
    }

    routeProgress?.let { progress ->
        LaunchedEffect(progress) {
            fetchRouteAndRender(mapboxMap?.style ?: return@LaunchedEffect)
        }
    }
}

private fun fetchRouteAndRender(
    style: Style
) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.mapbox.com/directions/v5/mapbox/driving/-122.431297,37.773972;-122.421778,37.759703?access_token=YOUR_MAPBOX_ACCESS_TOKEN")
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val directionsResponse = response.body?.string()
                val route = DirectionsResponse.fromJson(directionsResponse.toString())?.routes()?.firstOrNull()
                route?.let {
                    val routeGeometry = it.geometry()
                    val points = PolylineUtils.decode(routeGeometry!!, 6).map {
                        Point.fromLngLat(it.longitude(), it.latitude())
                    }
                    val routeLine = LineString.fromLngLats(points)
                    drawRouteOnMap(style, routeLine)
                }
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }
    })
}

private fun drawRouteOnMap(style: Style, routeLine: LineString) {
    val geoJsonSource = style.getSourceAs<GeoJsonSource>("route-source")
    if (geoJsonSource != null) {
        geoJsonSource.geometry(routeLine)
    } else {
        val newGeoJsonSource = GeoJsonSource.Builder("route-source")
            .geometry(routeLine)
            .build()
        style.addSource(newGeoJsonSource)
    }

    val lineColor = Color.parseColor("#FF0000")
    val lineLayer = style.getLayerAs<LineLayer>("route-layer")
    if (lineLayer == null) {
        style.addLayer(
            LineLayer("route-layer", "route-source").apply {
                lineColor(Expression.color(lineColor))
                lineWidth(5.0)
                lineCap(LineCap.ROUND)
                lineJoin(LineJoin.ROUND)
            }
        )
    }
}
