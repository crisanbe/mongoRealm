package com.cvelezg.metro.mongodemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.cvelezg.metro.mongodemo.navigation.Screen
import com.cvelezg.metro.mongodemo.navigation.SetupNavGraph
import com.cvelezg.metro.mongodemo.screen.auth.AuthenticationViewModel
import com.cvelezg.metro.mongodemo.ui.theme.MongoDemoTheme
import com.cvelezg.metro.mongodemo.util.Constants.APP_ID
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MongoDemoTheme {
                val navController = rememberNavController()
                val viewModel: AuthenticationViewModel = viewModel()

                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController
                )

                if (viewModel.authenticated.value) {
                    navController.navigate(Screen.Home.route)
                }
            }
        }
    }
}

private fun getStartDestination(): String {
    val user = App.create(APP_ID).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}