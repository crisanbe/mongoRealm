package com.cvelezg.metro.mongodemo.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cvelezg.metro.mongodemo.screen.auth.AuthenticationScreen
import com.cvelezg.metro.mongodemo.screen.auth.AuthenticationViewModel
import com.cvelezg.metro.mongodemo.screen.home.HomeScreen
import com.cvelezg.metro.mongodemo.screen.home.HomeViewModel
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
        homeRoute()
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

fun NavGraphBuilder.homeRoute() {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        val data by viewModel.data
        HomeScreen(
            filtered = viewModel.filtered.value,
            name = viewModel.name.value,
            objectId = viewModel.objectId.value,
            onNameChanged = viewModel::updateName,
            onObjectIdChanged = viewModel::updateObjectId,
            onInsertClicked = viewModel::insertPerson,
            onUpdateClicked = viewModel::updatePerson,
            onDeleteClicked = viewModel::deletePerson,
            onFilterClicked = viewModel::filterData
        )
    }
}