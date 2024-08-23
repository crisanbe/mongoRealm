// MainActivity.kt
package com.cvelezg.metro.mongodemo

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.cvelezg.metro.mongodemo.model.LocationData
import com.cvelezg.metro.mongodemo.navigation.Screen
import com.cvelezg.metro.mongodemo.navigation.SetupNavGraph
import com.cvelezg.metro.mongodemo.ui.theme.MongoDemoTheme
import com.cvelezg.metro.mongodemo.util.Constants.APP_ID
import com.cvelezg.metro.mongodemo.screen.location.LocationViewModel
import com.cvelezg.metro.mongodemo.util.calculateDistance
import com.mapbox.common.MapboxOptions
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.ObjectId

class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var locationViewModel: LocationViewModel

    private var lastSavedLocation: LocationData? = null
    private val minDistance = 50.0 // Distancia mínima en metros para guardar una nueva ubicación

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "Location permissions are required", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)

        // Initialize LocationManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(this@MainActivity).build()
            }
        }

        // Request location permissions
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        setContent {
            MongoDemoTheme {
                val navController = rememberNavController()
                locationViewModel = viewModel()

                Scaffold(
                    content = {
                        SetupNavGraph(
                            startDestination = getStartDestination(),
                            navController = navController
                        )
                    }
                )

                ObserveLocationUpdates(locationViewModel)
                StartLocationUpdates()
            }
        }
    }

    @Composable
    fun ObserveLocationUpdates(locationViewModel: LocationViewModel) {
        LaunchedEffect(Unit) {
            locationViewModel.observeLocationUpdates { locationData ->
                updateMap(locationData)
            }
        }
    }

    @Composable
    fun StartLocationUpdates() {
        LaunchedEffect(Unit) {
            startLocationUpdates()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val newLocationData = LocationData().apply {
                    latitude = location.latitude
                    longitude = location.longitude
                    owner_id = App.create(APP_ID).currentUser?.id ?: ""
                    timestamp = RealmInstant.now()
                }

                if (lastSavedLocation == null || calculateDistance(
                        lastSavedLocation!!.latitude, lastSavedLocation!!.longitude,
                        newLocationData.latitude, newLocationData.longitude
                    ) > minDistance) {
                    lastSavedLocation = newLocationData
                    locationViewModel.updateLocation(newLocationData)
                    Toast.makeText(this@MainActivity, "Location updated: ${newLocationData.latitude}, ${newLocationData.longitude}", Toast.LENGTH_SHORT).show()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L, // Intervalo en milisegundos
            1f, // Distancia mínima en metros para actualizaciones
            locationListener
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

    private fun getStartDestination(): String {
        val user = App.create(APP_ID).currentUser
        return if (user != null && user.loggedIn) Screen.Home.route
        else Screen.Authentication.route
    }

    private fun updateMap(locationData: LocationData) {
        // Logic to update the map with the new location data
        Toast.makeText(this, "Location updated: ${locationData.latitude}, ${locationData.longitude}", Toast.LENGTH_SHORT).show()
    }
}