package com.cvelezg.metro.mongodemo.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cvelezg.metro.mongodemo.R
import com.cvelezg.metro.mongodemo.util.componets.MetroLoadingScreen
import kotlinx.coroutines.delay

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
    var showLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(3000) // Simulate loading delay
        showLoading = false
    }

    if (showLoading) {
        MetroLoadingScreen()
    } else {
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
                painter = painterResource(id = R.drawable.medellin),
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