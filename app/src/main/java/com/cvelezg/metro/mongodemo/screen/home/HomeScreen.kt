@file:OptIn(ExperimentalMaterial3Api::class)

package com.cvelezg.metro.mongodemo.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cvelezg.metro.mongodemo.R
import com.cvelezg.metro.mongodemo.model.Person
import com.cvelezg.metro.mongodemo.ui.theme.GreenPrimary
import com.cvelezg.metro.mongodemo.util.componets.CurvedBackground
import com.cvelezg.metro.mongodemo.util.componets.PersonCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    name: String,
    objectId: String,
    onNameChanged: (String, String) -> Unit,
    onLogoutClicked: () -> Unit,
    onMapClicked: () -> Unit,
    onClearFields: () -> Unit,
    onInsertClicked: () -> Unit // Callback para insertar
) {
    val viewModel: HomeViewModel = viewModel()
    val data by viewModel.data
    val isLoading by viewModel.isLoading

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Scaffold(
            modifier = Modifier,
            floatingActionButton = {
                Box(){
                    FloatingActionButton(
                        onClick = { onInsertClicked() },
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(65.dp)
                            .offset(y = 50.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddCircle,
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            containerColor = Color(0xFF558B2F),
            bottomBar = {
                BottomAppBar(
                    actions = {
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
                    }
                )
            },
            topBar = {
                SmallTopAppBar(
                    title = { Text("Manage People🚊") },
                    actions = {
                        IconButton(onClick = onLogoutClicked) {
                            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout")
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = Color(0xFF558B2F),
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        ) {
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
                                // Manejar error si es necesario
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
                                // Manejar error si es necesario
                                println("Delete Error: $error")
                            }
                        )
                    }
                )
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
    onDeleteClicked: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // Input Fields and Buttons
        Column {
            Spacer(modifier = Modifier.height(45.dp))
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
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color(0xFFFFFFFF),
                        unfocusedBorderColor = Color(0xFFFFFFFF),
                        cursorColor = Color(0xFFFFFFFF)
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
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
                    isLoading =  isLoading,
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
        onInsertClicked = {}
    )
}