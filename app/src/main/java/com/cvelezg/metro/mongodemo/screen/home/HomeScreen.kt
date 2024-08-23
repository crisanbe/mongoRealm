// HomeScreen.kt
package com.cvelezg.metro.mongodemo.screen.home

import android.annotation.SuppressLint
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cvelezg.metro.mongodemo.model.Person
import com.cvelezg.metro.mongodemo.util.componets.PersonCard

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    filtered: Boolean,
    name: String,
    objectId: String,
    onNameChanged: (String) -> Unit,
    onObjectIdChanged: (String) -> Unit,
    onInsertClicked: () -> Unit,
    onUpdateClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onMapClicked: () -> Unit // Add this parameter
) {
    val viewModel: HomeViewModel = viewModel()
    val data by viewModel.data

    Scaffold(
        content = {
            HomeContent(
                data = data,
                filtered = filtered,
                name = name,
                objectId = objectId,
                onNameChanged = onNameChanged,
                onObjectIdChanged = onObjectIdChanged,
                onInsertClicked = onInsertClicked,
                onUpdateClicked = onUpdateClicked,
                onDeleteClicked = onDeleteClicked,
                onFilterClicked = onFilterClicked,
                onLogoutClicked = onLogoutClicked,
                onMapClicked = onMapClicked // Pass the parameter
            )
        }
    )
}

@Composable
fun HomeContent(
    data: List<Person>,
    filtered: Boolean,
    name: String,
    objectId: String,
    onNameChanged: (String) -> Unit,
    onObjectIdChanged: (String) -> Unit,
    onInsertClicked: () -> Unit,
    onUpdateClicked: () -> Unit,
    onDeleteClicked: () -> Unit,
    onFilterClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    onMapClicked: () -> Unit // Add this parameter
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Row {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = objectId,
                    onValueChange = onObjectIdChanged,
                    placeholder = { Text(text = "Object ID") }
                )
                Spacer(modifier = Modifier.width(12.dp))
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = name,
                    onValueChange = onNameChanged,
                    placeholder = { Text(text = "Name") }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(state = rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onInsertClicked) {
                    Text(text = "Add")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = onUpdateClicked) {
                    Text(text = "Update")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = onDeleteClicked) {
                    Text(text = "Delete")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = onFilterClicked) {
                    Text(text = if (filtered) "Clear" else "Filter")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = onLogoutClicked) {
                    Text(text = "Logout")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(onClick = onMapClicked) { // Add this button
                    Text(text = "Map")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = data, key = { it._id.toHexString() }) {
                PersonCard(
                    id = it._id.toHexString(),
                    name = it.name,
                    timestamp = it.timestamp
                )
            }
        }
    }
}