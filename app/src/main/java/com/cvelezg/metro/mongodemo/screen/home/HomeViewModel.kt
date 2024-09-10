package com.cvelezg.metro.mongodemo.screen.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvelezg.metro.mongodemo.data.MongoDB
import com.cvelezg.metro.mongodemo.model.Person
import com.cvelezg.metro.mongodemo.model.Address
import com.cvelezg.metro.mongodemo.model.Pet
import com.cvelezg.metro.mongodemo.util.Constants.APP_ID
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class HomeViewModel : ViewModel() {
    var name = mutableStateOf("")
    var objectId = mutableStateOf("")
    var filtered = mutableStateOf(false)
    var data = mutableStateOf(emptyList<Person>())
    var isLoading = mutableStateOf(false)
    private val realm: Realm

    init {
        val config = RealmConfiguration.Builder(schema = setOf(Person::class, Address::class, Pet::class))
            .name("person.realm")
            .build()
        realm = Realm.open(config)

        viewModelScope.launch {
            MongoDB.realmInitialized.collect { initialized ->
                if (initialized) {
                    MongoDB.getData().collect { data ->
                        this@HomeViewModel.data.value = data
                    }
                }
            }
        }
    }

    // Refrescar datos desde MongoDB
    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MongoDB.refreshData()
            } catch (e: Exception) {
                e.printStackTrace()  // Manejo de error al refrescar datos
            }
        }
    }

    // Actualizar el nombre de la persona
    fun updateName(name: String) {
        this.name.value = name
    }

    fun updateObjectId(id: String) {
        this.objectId.value = id
    }

    // Insertar una persona en MongoDB
    fun insertPerson(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (name.value.isNotEmpty()) {
                    MongoDB.insertPerson(person = Person().apply {
                        name = this@HomeViewModel.name.value
                    })
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    // Insertar múltiples personas (1,000 personas)
    fun insertTenThousandData(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repeat(1_000) { index ->
                    val person = Person().apply {
                        name = "Tramo $index"
                    }
                    MongoDB.insertPerson(person)
                }
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    // Actualizar la persona en MongoDB
    fun updatePerson(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            try {
                if (objectId.value.isNotEmpty()) {
                    MongoDB.updatePerson(person = Person().apply {
                        _id = ObjectId(hexString = this@HomeViewModel.objectId.value)
                        name = this@HomeViewModel.name.value
                    })
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    // Eliminar una persona
    fun deletePerson(id: String, name: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            try {
                if (id.isNotEmpty()) {
                    MongoDB.deletePerson(id = ObjectId(hexString = id))
                    withContext(Dispatchers.Main) {
                        onSuccess()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            } finally {
                isLoading.value = false
            }
        }
    }

    // Eliminar todas las personas
    fun deleteAllPersons(onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MongoDB.getData().collect { personList ->
                    personList.forEach { person ->
                        MongoDB.deletePerson(person._id)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    // Filtrar personas según el nombre
    fun filterData() {
        viewModelScope.launch(Dispatchers.IO) {
            if (filtered.value) {
                MongoDB.getData().collect {
                    filtered.value = false
                    name.value = ""
                    data.value = it
                }
            } else {
                MongoDB.filterData(name = name.value).collect {
                    filtered.value = true
                    data.value = it
                }
            }
        }
    }

    // Cerrar sesión y limpiar la base de datos local
    fun logout(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = App.create(APP_ID).currentUser
                user?.logOut()
                MongoDB.clearLocalRealm()
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }

    // Limpiar campos del formulario
    fun clearFields() {
        name.value = ""
        objectId.value = ""
    }

    // Cerrar Realm cuando el ViewModel se destruye
    override fun onCleared() {
        super.onCleared()
        realm.close()
    }
}
