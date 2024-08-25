@file:OptIn(ExperimentalMaterial3Api::class)

package com.cvelezg.metro.mongodemo.util.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cvelezg.metro.mongodemo.ui.theme.GreenPrimary
import io.realm.kotlin.types.RealmInstant
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

@Composable
fun PersonCard(
    id: String,
    name: String,
    age: Int,
    timestamp: RealmInstant,
    isLoading: Boolean, // Añadido para el estado de carga
    onDeleteClicked: (String, String) -> Unit, // Callback con ID y nombre
    onNameChanged: (String, String) -> Unit // Callback para actualizar nombre
) {
    var editedName by remember { mutableStateOf(name) }
    LaunchedEffect(name) {
        editedName = name
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .border(2.dp, GreenPrimary, RoundedCornerShape(16.dp)), // Borde verde
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp) // Esquinas redondeadas
    ) {
        Box {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = editedName,
                        onValueChange = { editedName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF558B2F) // Texto verde
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = GreenPrimary, // Borde verde al enfocar
                            unfocusedBorderColor = Color.Gray, // Borde gris al no enfocar
                            cursorColor = GreenPrimary // Cursor verde
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    SelectionContainer {
                        Text(
                            text = id,
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFFFFFFFF)
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.width(5.dp))
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = SimpleDateFormat("MMM d, yyyy - hh:mm a", Locale.getDefault())
                            .format(Date.from(Instant.ofEpochMilli(timestamp.epochSeconds * 1000))),
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9E9D24)
                        ),
                        modifier = Modifier.padding(bottom = 8.dp) // Espacio debajo de la fecha
                    )
                    Text(
                        text = "Age: $age",
                        style = TextStyle(
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF9E9D24)
                        )
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onDeleteClicked(id, name) },
                            modifier = Modifier.size(24.dp) // Tamaño del ícono de eliminar
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete",
                                tint = Color.Red // Color del ícono de eliminar
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { onNameChanged(id, editedName) }) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit",
                                tint = Color.Blue // Color del ícono de editar
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp),
                    strokeWidth = 2.dp
                )
            }
        }
    }
}
