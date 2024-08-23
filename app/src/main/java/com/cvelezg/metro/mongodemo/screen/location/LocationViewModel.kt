// LocationViewModel.kt
package com.cvelezg.metro.mongodemo.screen.location

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cvelezg.metro.mongodemo.data.MongoDB
import com.cvelezg.metro.mongodemo.model.LocationData
import com.cvelezg.metro.mongodemo.model.Person
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val realm: Realm
    var data = mutableStateOf(emptyList<LocationData>())

    init {
        val config = RealmConfiguration.Builder(schema = setOf(LocationData::class))
            .name("location.realm")
            .build()
        realm = Realm.open(config)
    }

    fun updateLocation(location: LocationData) {
        viewModelScope.launch {
            realm.write {
                val existingLocationData = query<LocationData>().first().find()
                if (existingLocationData != null) {
                    existingLocationData.latitude = location.latitude
                    existingLocationData.longitude = location.longitude
                    existingLocationData.timestamp = location.timestamp
                } else {
                    copyToRealm(location)
                }
            }
            MongoDB.insertLocation(location)
        }
    }

    fun observeLocationUpdates(onLocationUpdated: (LocationData) -> Unit) {
        viewModelScope.launch {
            realm.query<LocationData>().asFlow().collect { changes: ResultsChange<LocationData> ->
                changes.list.firstOrNull()?.let { onLocationUpdated(it) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}