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
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

object MongoDB : MongoRepository {
    private val app = App.create(APP_ID)
    private val user get() = app.currentUser
    private val _realmInitialized = MutableStateFlow(false)
    val realmInitialized: StateFlow<Boolean> get() = _realmInitialized
    private lateinit var realm: Realm

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Espera a que el usuario esté autenticado
                while (user == null) {
                    delay(1000)
                }
                configureTheRealm()
                _realmInitialized.value = true
            } catch (e: Exception) {
                Log.e("MongoDB", "Initialization error: ${e.message}")
            }
        }
    }

    override suspend fun configureTheRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(
                user!!,
                setOf(Person::class, Address::class, Pet::class, LocationData::class)
            )
                .initialSubscriptions { sub ->
                    add(query = sub.query<Person>("owner_id == $0", user!!.id))
                    add(query = sub.query<LocationData>("owner_id == $0", user!!.id))
                }
                .log(LogLevel.ALL)
                .schemaVersion(1)
                .build()

            try {
                realm = Realm.open(config)
                // Forzar sincronización inicial
                realm.subscriptions.waitForSynchronization()
            } catch (e: Exception) {
                Log.e("MongoDB", "Failed to configure Realm: ${e.message}")
            }
        } else {
            Log.e("MongoDB", "User is not authenticated.")
        }
    }

    fun clearLocalRealm() {
        user?.let {
            val config = SyncConfiguration.Builder(
                it,
                setOf(Person::class, Address::class, Pet::class, LocationData::class)
            ).build()

            try {
                Realm.deleteRealm(config)  // Borra la base de datos local de Realm
            } catch (e: Exception) {
                Log.e("MongoDB", "Failed to delete Realm database: ${e.message}")
            }
        }
    }

    override fun getData(): Flow<List<Person>> {
        check(_realmInitialized.value) { "Realm has not been initialized yet" }
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
                    copyToRealm(person.apply { owner_id = user!!.id })
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