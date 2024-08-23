package com.cvelezg.metro.mongodemo.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Authentication : Screen("authentication_screen")
    object Map : Screen("map_screen")
}
