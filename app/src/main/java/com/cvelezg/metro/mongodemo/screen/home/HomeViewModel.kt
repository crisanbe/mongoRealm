// HomeViewModel.kt
package com.cvelezg.metro.mongodemo.screen.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cvelezg.metro.mongodemo.data.MongoDB
import com.cvelezg.metro.mongodemo.model.Person
import com.cvelezg.metro.mongodemo.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId

class HomeViewModel : ViewModel() {
    var name = mutableStateOf("")
    var objectId = mutableStateOf("")
    var filtered = mutableStateOf(false)
    var data = mutableStateOf(emptyList<Person>())

    // Estado de carga
    var isLoading = mutableStateOf(false)

    // Tiempo de retraso en milisegundos
    private val loadingDelay = 500L // 500 ms

    init {
        viewModelScope.launch {
            MongoDB.getData().collect {
                data.value = it
            }
        }
    }

    fun updateName(name: String) {
        this.name.value = name
    }

    fun updateObjectId(id: String) {
        this.objectId.value = id
    }

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

    fun updatePerson(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val loadingJob = launch {
                isLoading.value = true
            }

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
                // Completar el Job de carga, si no se ha completado aún
                loadingJob.cancel()
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    fun deletePerson(id: String, name: String, onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val loadingJob = launch {
                isLoading.value = true
            }

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
                // Completar el Job de carga, si no se ha completado aún
                loadingJob.cancel()
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

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

    fun logout(onSuccess: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = App.create(APP_ID).currentUser
                user?.logOut()
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

    // Function to clear fields
    fun clearFields() {
        name.value = ""
        objectId.value = ""
    }
}
