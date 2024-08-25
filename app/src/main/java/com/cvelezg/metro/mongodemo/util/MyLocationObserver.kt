/*
package com.cvelezg.metro.mongodemo.util

import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class MyLocationObserver : LocationObserver {

    // Flow para emitir actualizaciones de ubicación
    private val _locationFlow = MutableStateFlow<android.location.Location?>(null)
    val location: Flow<android.location.Location?> get() = _locationFlow

    override fun onNewLocationMatcherResult(result: LocationMatcherResult) {
        _locationFlow.value = result.enhancedLocation
    }

    override fun onNewRawLocation(rawLocation: android.location.Location) {
        _locationFlow.value = rawLocation
    }


}
*/
