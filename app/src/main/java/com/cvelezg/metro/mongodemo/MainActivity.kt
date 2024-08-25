// MainActivity.kt
package com.cvelezg.metro.mongodemo

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.cvelezg.metro.mongodemo.model.LocationData
import com.cvelezg.metro.mongodemo.navigation.Screen
import com.cvelezg.metro.mongodemo.navigation.SetupNavGraph
import com.cvelezg.metro.mongodemo.screen.location.LocationViewModel
import com.cvelezg.metro.mongodemo.ui.theme.MongoDemoTheme
import com.cvelezg.metro.mongodemo.util.Constants.APP_ID
//import com.cvelezg.metro.mongodemo.util.MyMapboxNavigationObserver
//import com.cvelezg.metro.mongodemo.util.androidauto.MainCarAppService
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.types.RealmInstant

class MainActivity : ComponentActivity() {
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var locationViewModel: LocationViewModel
    private var lastSavedLocation: Location? = null
    //private lateinit var mapboxObserver: MyMapboxNavigationObserver

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

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                MapboxNavigationApp.attach(owner)
            }

            override fun onPause(owner: LifecycleOwner) {
                MapboxNavigationApp.detach(owner)
            }
        })
    }

    @SuppressLint("MissingPermission", "UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicializa el observador con el contexto de la aplicación
       // mapboxObserver = MyMapboxNavigationObserver(applicationContext)

        // Initialize LocationManager
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (!MapboxNavigationApp.isSetup()) {
            MapboxNavigationApp.setup {
                NavigationOptions.Builder(applicationContext)
                    //.accessToken(getString(R.string.mapbox_access_token))
                    .build()
            }
        }
        // Registra el observador
        //MapboxNavigationApp.registerObserver(mapboxObserver)

     /*   lifecycleScope.launchWhenStarted {
            mapboxObserver.location.collect { location ->
                location?.let {
                    Log.d("LocationUpdate", "Lat: ${it.latitude}, Lon: ${it.longitude}")
                }
            }
        }*/

        // Request location permissions
        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        // Inicia el servicio en primer plano
       /* val serviceIntent = Intent(this, MainCarAppService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)*/
        setContent {
            val darkColors = remember { mutableStateOf(true) }

            MongoDemoTheme(
                darkTheme = darkColors.value,
            ) {
                val navController = rememberNavController()
                locationViewModel = viewModel()
                var showDialog by remember { mutableStateOf(false) }
                var latitude by remember { mutableStateOf(0.0) }
                var longitude by remember { mutableStateOf(0.0) }

                Scaffold(
                    content = {
                        SetupNavGraph(
                            startDestination = getStartDestination(),
                            navController = navController
                        )
                    }
                )

                ObserveLocationUpdates(locationViewModel) { newLocationData ->
                    latitude = newLocationData.latitude
                    longitude = newLocationData.longitude
                    showDialog = true
                }

                StartLocationUpdates()

               /* if (showDialog) {
                    AlertDialog(
                        onDismissRequest = { showDialog = false },
                        confirmButton = {
                            Button(onClick = { showDialog = false }) {
                                Text("Close")
                            }
                        },
                        text = {
                            Text("Location updated:\nLatitude: $latitude\nLongitude: $longitude")
                        },
                        dismissButton = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                modifier = Modifier.clickable { showDialog = false }
                            )
                        }
                    )
                }*/
            }
        }
    }

    @Composable
    fun ObserveLocationUpdates(locationViewModel: LocationViewModel, onLocationUpdate: (LocationData) -> Unit) {
        LaunchedEffect(Unit) {
            locationViewModel.observeLocationUpdates { locationData ->
                updateMap(locationData)
                onLocationUpdate(locationData)
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
                if (lastSavedLocation == null || location.distanceTo(lastSavedLocation!!) >= 50) {
                    lastSavedLocation = location
                    val newLocationData = LocationData().apply {
                        latitude = location.latitude
                        longitude = location.longitude
                        owner_id = App.create(APP_ID).currentUser?.id ?: ""
                        timestamp = RealmInstant.now()
                    }

                    locationViewModel.updateLocation(newLocationData)
                    Toast.makeText(this@MainActivity, "Location updated: ${newLocationData.latitude}, ${newLocationData.longitude}", Toast.LENGTH_LONG).show()
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
        //MapboxNavigationApp.unregisterObserver(mapboxObserver)
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