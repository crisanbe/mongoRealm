// MainActivity.kt
package com.cvelezg.metro.mongodemo

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.cvelezg.metro.mongodemo.data.network.LocationManager
import com.cvelezg.metro.mongodemo.model.LocationData
import com.cvelezg.metro.mongodemo.navigation.Screen
import com.cvelezg.metro.mongodemo.navigation.SetupNavGraph
import com.cvelezg.metro.mongodemo.screen.location.LocationViewModel
import com.cvelezg.metro.mongodemo.ui.theme.MongoDemoTheme
import com.cvelezg.metro.mongodemo.util.Constants.APP_ID
import com.cvelezg.metro.mongodemo.util.calculateDistance
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.types.RealmInstant
import org.mongodb.kbson.ObjectId

class MainActivity : ComponentActivity() {
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var myLocationManager: LocationManager
    private var lastSavedLocation: LocationData? = null
    private val minDistance = 50.0 // Distancia mínima en metros para guardar una nueva ubicación

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            startLocationUpdates()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar LocationManager desacoplado
        myLocationManager = LocationManager(this)

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
        myLocationManager.getLocation { location ->
            val newLocationData = LocationData().apply {
                latitude = location.latitude
                longitude = location.longitude
                timestamp = RealmInstant.now()
                _id = ObjectId()
                owner_id = App.create(APP_ID).currentUser?.id ?: ""
            }

            if (lastSavedLocation == null) {
                // Guardar la ubicación inicial
                lastSavedLocation = newLocationData
                locationViewModel.updateLocation(newLocationData)
            } else {
                val distance = calculateDistance(
                    lastSavedLocation!!.latitude, lastSavedLocation!!.longitude,
                    newLocationData.latitude, newLocationData.longitude
                )

                if (distance >= minDistance) {
                    // Guardar la nueva ubicación si la distancia es mayor o igual al mínimo
                    lastSavedLocation = newLocationData
                    locationViewModel.updateLocation(newLocationData)
                }
            }
        }
    }

    private fun getStartDestination(): String {
        val user = App.create(APP_ID).currentUser
        return if (user != null && user.loggedIn) Screen.Home.route
        else Screen.Authentication.route
    }

    private fun updateMap(locationData: LocationData) {
        // Actualizar mapa o UI con los nuevos datos de ubicación
        Toast.makeText(this, "Location updated: ${locationData.latitude}, ${locationData.longitude}", Toast.LENGTH_SHORT).show()
    }
}
