package com.cvelezg.metro.mongodemo.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvelezg.metro.mongodemo.R

@Composable
fun AuthenticationContent(
    email: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    loadingState: Boolean,
    onButtonClicked: () -> Unit,
    onEmailPasswordSignIn: (String, String) -> Unit,
    onConfirmUser: (String, String, String) -> Unit
) {
    var emailState by remember { mutableStateOf(email) }
    var passwordState by remember { mutableStateOf(password) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.size(120.dp),
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "App Logo"
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            text = "Please sign in to continue.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = emailState,
            onValueChange = {
                emailState = it
                onEmailChanged(it)
            },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = passwordState,
            onValueChange = {
                passwordState = it
                onPasswordChanged(it)
            },
            label = { Text("Password") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = { onEmailPasswordSignIn(emailState, passwordState) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In with Email")
        }
        Spacer(modifier = Modifier.height(10.dp))
        GoogleButton(
            loadingState = loadingState,
            onClick = onButtonClicked
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = "",
            onValueChange = { /* Handle token change */ },
            label = { Text("Token") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        TextField(
            value = "",
            onValueChange = { /* Handle tokenId change */ },
            label = { Text("Token ID") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onConfirmUser(emailState, "token", "tokenId") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm User")
        }
    }
}

@Composable
@Preview
fun AuthenticationContentPreview() {
    AuthenticationContent(
        email = "",
        password = "",
        onEmailChanged = { },
        onPasswordChanged = { },
        loadingState = false,
        onButtonClicked = { },
        onEmailPasswordSignIn = { _, _ -> },
        onConfirmUser = { _, _, _ -> }
    )
}