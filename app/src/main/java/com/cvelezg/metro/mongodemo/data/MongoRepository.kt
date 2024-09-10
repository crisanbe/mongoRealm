package com.cvelezg.metro.mongodemo.data

import com.cvelezg.metro.mongodemo.model.LocationData
import com.cvelezg.metro.mongodemo.model.Person
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId

interface MongoRepository {
    suspend fun configureTheRealm()
    suspend fun refreshData()
    fun getData(): Flow<List<Person>>
    fun filterData(name: String): Flow<List<Person>>
    suspend fun insertPerson(person: Person)
    suspend fun updatePerson(person: Person)
    suspend fun deletePerson(id: ObjectId)
    suspend fun getLocationData() : Flow<List<LocationData>>
    suspend fun insertLocation(locationData: LocationData)

}