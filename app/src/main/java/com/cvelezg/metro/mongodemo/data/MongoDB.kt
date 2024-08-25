// MongoDB.kt
package com.cvelezg.metro.mongodemo.data

import android.util.Log
import com.cvelezg.metro.mongodemo.model.Address
import com.cvelezg.metro.mongodemo.model.LocationData
import com.cvelezg.metro.mongodemo.model.Person
import com.cvelezg.metro.mongodemo.model.Pet
import com.cvelezg.metro.mongodemo.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

object MongoDB : MongoRepository {
    private val app = App.create(APP_ID)
    private val user = app.currentUser
    private lateinit var realm: Realm

    init {
        configureTheRealm()
    }

    override fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user,
                setOf(Person::class, Address::class, Pet::class, LocationData::class)
            )
                .initialSubscriptions { sub ->
                    add(query = sub.query<Person>(query = "owner_id == $0", user.id))
                    add(query = sub.query<LocationData>(query = "owner_id == $0", user.id))
                }
                .log(LogLevel.ALL)
                .schemaVersion(1) // Actualiza esta versión según sea necesario
                .build()
            realm = Realm.open(config)
        }
    }


    fun deleteRealmDatabase() {
        val config = SyncConfiguration.Builder(
            user!!,
            setOf(Person::class, Address::class, Pet::class, LocationData::class)
        )
            .build()
        Realm.deleteRealm(config)
    }

    override fun getData(): Flow<List<Person>> {
        return realm.query<Person>().asFlow().map { it.list }
    }

    override fun filterData(name: String): Flow<List<Person>> {
        return realm.query<Person>(query = "name CONTAINS[c] $0", name)
            .asFlow().map { it.list }
    }

    override suspend fun insertPerson(person: Person) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(person.apply { owner_id = user.id })
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }

    override suspend fun updatePerson(person: Person) {
        realm.write {
            val queriedPerson =
                query<Person>(query = "_id == $0", person._id)
                    .first()
                    .find()
            if (queriedPerson != null) {
                queriedPerson.name = person.name
            } else {
                Log.d("MongoRepository", "Queried Person does not exist.")
            }
        }
    }

    override suspend fun deletePerson(id: ObjectId) {
        realm.write {
            try {
                val person = query<Person>(query = "_id == $0", id)
                    .first()
                    .find()
                person?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("MongoRepository", "${e.message}")
            }
        }
    }

    // New methods for LocationData
    override suspend fun getLocationData(): Flow<List<LocationData>> {
        return realm.query<LocationData>().asFlow().map { it.list }
    }

    override suspend fun insertLocation(locationData: LocationData) {
        if (user != null) {
            realm.write {
                try {
                    copyToRealm(locationData)
                } catch (e: Exception) {
                    Log.d("MongoRepository", e.message.toString())
                }
            }
        }
    }
}