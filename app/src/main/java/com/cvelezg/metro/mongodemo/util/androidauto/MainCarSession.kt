/*
package com.cvelezg.metro.mongodemo.util.androidauto

import android.content.Intent
import android.content.res.Configuration
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.cvelezg.metro.mongodemo.R
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.androidauto.MapboxCarContext
import com.mapbox.androidauto.deeplink.GeoDeeplinkNavigateAction
import com.mapbox.androidauto.map.MapboxCarMapLoader
import com.mapbox.androidauto.notification.MapboxCarNotificationOptions
import com.mapbox.androidauto.screenmanager.MapboxScreen
import com.mapbox.androidauto.screenmanager.MapboxScreenManager
import com.mapbox.androidauto.screenmanager.prepareScreens
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.extension.androidauto.MapboxCarMap
import com.mapbox.navigation.base.ExperimentalPreviewMapboxNavigationAPI
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp

@OptIn(ExperimentalPreviewMapboxNavigationAPI::class)
class MainCarSession : Session() {

  private val carMapLoader = MapboxCarMapLoader()
    private val mapboxCarMap = MapboxCarMap().registerObserver(carMapLoader)
    private val mapboxCarContext = MapboxCarContext(lifecycle, mapboxCarMap)

    init {
        MapboxNavigationApp.attach(lifecycleOwner = this)

        mapboxCarContext.prepareScreens()

        mapboxCarContext.customize {
            notificationOptions = MapboxCarNotificationOptions.Builder()
                .startAppService(MainCarAppService::class.java)
                .build()
        }

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                if (!MapboxNavigationApp.isSetup()) {
                    MapboxNavigationApp.setup(
                        NavigationOptions.Builder(carContext)
                            .accessToken(carContext.getString(R.string.mapbox_access_token))
                            .build()
                    )
                }

                mapboxCarMap.setup(carContext, MapInitOptions(context = carContext))
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapboxCarMap.clearObservers()
            }
        })
    }

    override fun onCreateScreen(intent: Intent): Screen {
        val firstScreenKey = if (PermissionsManager.areLocationPermissionsGranted(carContext)) {
            MapboxScreenManager.current()?.key ?: MapboxScreen.FREE_DRIVE
        } else {
            MapboxScreen.NEEDS_LOCATION_PERMISSION
        }
        return mapboxCarContext.mapboxScreenManager.createScreen(firstScreenKey)
    }

    override fun onCarConfigurationChanged(newConfiguration: Configuration) {
        carMapLoader.getStyleExtension(carContext.isDarkMode)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        GeoDeeplinkNavigateAction(mapboxCarContext).onNewIntent(intent)
    }

}
*/
