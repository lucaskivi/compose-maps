package com.example.composemaps.ui.utils

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Destinations {
    abstract val key: String

    object Search: Destinations() {
        override val key: String
            get() = "search"
    }
}

@Composable
fun ScreenDispatcher() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Destinations.Search.key) {
        composable(route = Destinations.Search.key) {
        }
    }
}
