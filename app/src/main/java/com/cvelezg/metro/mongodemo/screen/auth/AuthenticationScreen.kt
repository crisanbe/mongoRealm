package com.cvelezg.metro.mongodemo.screen.auth

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.cvelezg.metro.mongodemo.util.Constants.CLIENT_ID
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    authenticated: Boolean,
    loadingState: Boolean,
    oneTapState: OneTapSignInState,
    messageBarState: MessageBarState,
    onButtonClicked: () -> Unit,
    onSuccessfulSignIn: (String) -> Unit,
    onDialogDismissed: (String) -> Unit,
    onEmailPasswordSignIn: (String, String) -> Unit,
    onConfirmUser: (String, String, String) -> Unit,
    navigateToHome: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .navigationBarsPadding(),
        content = {
            ContentWithMessageBar(messageBarState = messageBarState) {
                AuthenticationContent(
                    email = "",
                    password = "",
                    onEmailChanged = { },
                    onPasswordChanged = { },
                    loadingState = loadingState,
                    onButtonClicked = onButtonClicked,
                    onEmailPasswordSignIn = { email, password ->
                        onEmailPasswordSignIn(email, password)
                    },
                    onConfirmUser = { username, token, tokenId ->
                        onConfirmUser(username, token, tokenId)
                    }
                )
            }
        }
    )

    OneTapSignInWithGoogle(
        state = oneTapState,
        clientId = CLIENT_ID,
        onTokenIdReceived = { tokenId ->
            onSuccessfulSignIn(tokenId)
        },
        onDialogDismissed = { message ->
            onDialogDismissed(message)
        }
    )

    LaunchedEffect(key1 = authenticated) {
        if (authenticated) {
            navigateToHome()
        }
    }
}

/*
@ExperimentalMaterial3Api
@Composable
@Preview
fun AuthenticationScreenPreview() {
    AuthenticationScreen(
        authenticated = false,
        loadingState = false,
        oneTapState = OneTapSignInState(),
        messageBarState = MessageBarState(),
        onButtonClicked = { },
        onSuccessfulSignIn = { _ -> },
        onDialogDismissed = { _ -> },
        onEmailPasswordSignIn = { _, _ -> },
        onConfirmUser = { _, _, _ -> },
        navigateToHome = { }
    )
}
*/