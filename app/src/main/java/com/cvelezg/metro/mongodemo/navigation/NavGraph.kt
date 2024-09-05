package com.cvelezg.metro.mongodemo.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cvelezg.metro.mongodemo.screen.auth.AuthenticationScreen
import com.cvelezg.metro.mongodemo.screen.auth.AuthenticationViewModel
import com.cvelezg.metro.mongodemo.screen.home.HomeScreen
import com.cvelezg.metro.mongodemo.screen.home.HomeViewModel
import com.cvelezg.metro.mongodemo.util.componets.MapScreen
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState

@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authRoute(
            navigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            }
        )
        homeRoute(
            navigateToAuth = {
                navController.navigate(Screen.Authentication.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            },
            navigateToMap = {
                navController.navigate(Screen.Map.route)
            }
        )
        mapRoute()
    }
}

fun NavGraphBuilder.mapRoute() {
    composable(route = Screen.Map.route) {
        MapScreen(context = LocalContext.current)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authRoute(
    navigateToHome: () -> Unit,
) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val authenticated by viewModel.authenticated
        val loadingState by viewModel.loadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onSuccessfulSignIn = { tokenId ->
                viewModel.signInWithGoogle(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated!")
                        viewModel.setLoading(false)
                        navigateToHome()
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            },
            onEmailPasswordSignIn = { email, password ->
                viewModel.signInWithMongoAtlas(
                    email = email,
                    password = password,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated!")
                        viewModel.setLoading(false)
                        navigateToHome()
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            },
            onDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome,
            onConfirmUser = { username, token, tokenId ->
                viewModel.signInWithGoogle(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully Authenticated!")
                        viewModel.setLoading(false)
                        navigateToHome()
                    },
                    onError = {
                        messageBarState.addError(it)
                        viewModel.setLoading(false)
                    }
                )
            }
        )
    }
}

fun NavGraphBuilder.homeRoute(
    navigateToAuth: () -> Unit,
    navigateToMap: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        HomeScreen(
            name = viewModel.name.value,
            objectId = viewModel.objectId.value,
            onNameChanged = { id, newName ->
                viewModel.updateObjectId(id)
                viewModel.updateName(newName)
            },
            onLogoutClicked = {
                viewModel.logout(onSuccess = {
                    navigateToAuth()
                }, onError = {
                    // Handle error if needed
                })
            },
            onMapClicked = navigateToMap,
            onClearFields = viewModel::clearFields,
            onInsertClicked = {
                viewModel.insertPerson(
                    onSuccess = {
                        viewModel.clearFields()
                    },
                    onError = {
                        // Handle error if needed
                    }
                )
            },
        )
    }
}
