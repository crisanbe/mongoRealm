@file:OptIn(ExperimentalMaterial3Api::class)

package com.cvelezg.metro.mongodemo.screen.home

import android.annotation.SuppressLint
import android.provider.CalendarContract.Colors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cvelezg.metro.mongodemo.R
import com.cvelezg.metro.mongodemo.model.Person
import com.cvelezg.metro.mongodemo.util.componets.CurvedBackground
import com.cvelezg.metro.mongodemo.util.componets.PersonCard
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    name: String,
    objectId: String,
    onNameChanged: (String, String) -> Unit,
    onLogoutClicked: () -> Unit,
    onMapClicked: () -> Unit,
    onClearFields: () -> Unit,
    onInsertClicked: () -> Unit,
    onInsert100Clicked: () -> Unit,
    onDelete100Clicked: () -> Unit,
    onRefresh: () -> Unit
) {
    val viewModel: HomeViewModel = viewModel()
    val data by viewModel.data
    val isLoading by viewModel.isLoading

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Manage People🚊") },
                    actions = {
                        IconButton(onClick = onLogoutClicked) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF558B2F),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    IconButton(
                        onClick = onMapClicked,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(70.dp)
                            .padding(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.gps),
                            contentDescription = "Geolocalización"
                        )
                    }

                    FloatingActionButton(
                        onClick = { onInsertClicked() },
                        modifier = Modifier.size(60.dp),
                        containerColor = Color(0xFF558B2F)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = "Insertar uno",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { onInsert100Clicked() },
                        modifier = Modifier.size(60.dp),
                        containerColor = Color(0xFFFDD835)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Insertar 100",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    FloatingActionButton(
                        onClick = { onDelete100Clicked() },
                        modifier = Modifier.size(60.dp),
                        containerColor = Color.Red
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = "Eliminar todos",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Mostrar el total de datos
                Text(
                    text = "Total de datos: ${data.size}",
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color(0xFF558B2F))
                        .padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )

                Box(modifier = Modifier.fillMaxSize()) {
                    CurvedBackground()

                    // Contenido de la pantalla
                    HomeContent(
                        data = data,
                        name = name,
                        isLoading = isLoading,
                        objectId = objectId,
                        onNameChanged = { id, newName -> onNameChanged(id, newName) },
                        onInsertClicked = onInsertClicked,
                        onUpdateClicked = {
                            viewModel.updatePerson(
                                onSuccess = {
                                    onClearFields() // Limpiar campos después de actualizar
                                },
                                onError = { error ->
                                    println("Update Error: $error")
                                }
                            )
                        },
                        onDeleteClicked = { id, name ->
                            viewModel.deletePerson(id, name,
                                onSuccess = {
                                    onClearFields() // Limpiar campos después de eliminar
                                },
                                onError = { error ->
                                    println("Delete Error: $error")
                                }
                            )
                        },
                        onRefresh = { onRefresh()}
                    )
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    data: List<Person>,
    name: String,
    isLoading : Boolean,
    objectId: String,
    onNameChanged: (String, String) -> Unit,
    onInsertClicked: () -> Unit,
    onUpdateClicked: () -> Unit,
    onDeleteClicked: (String, String) -> Unit,
    onRefresh: () -> Unit
) {
    val refreshingState = SwipeRefreshState(isRefreshing = isLoading)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Input Fields and Buttons
        Column {
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = name,
                    onValueChange = { onNameChanged(objectId, it) },
                    label = { Text("Name") },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFFFFF)
                    ),
                    colors = androidx.compose.material3.TextFieldDefaults.colors(
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        SwipeRefresh(
            state = refreshingState,
            onRefresh = { onRefresh() }, // Llama a la función de refresco cuando se desliza
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(2.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(items = data, key = { it._id.toHexString() }) { person ->
                    PersonCard(
                        id = person._id.toHexString(),
                        name = person.name,
                        age = person.age,
                        timestamp = person.timestamp,
                        isLoading = isLoading,
                        onDeleteClicked = { id, name ->
                            onDeleteClicked(id, name)
                        },
                        onNameChanged = { id, editedName ->
                            onNameChanged(id, editedName)
                            onUpdateClicked()
                        }
                    )
                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewHomeScreen() {
    HomeScreen(
        name = "",
        objectId = "",
        onNameChanged = { _, _ -> },
        onLogoutClicked = {},
        onMapClicked = {},
        onClearFields = {},
        onInsertClicked = {},
        onInsert100Clicked = {},
        onDelete100Clicked = {},
        onRefresh = {}
    )
}