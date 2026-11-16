package com.prince.findthemischief

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prince.findthemischief.screens.home.HomeScreen
import kotlinx.serialization.Serializable

// Type-safe routes: each destination is a @Serializable object (or data class when it needs arguments).
@Serializable
data object HomeRoute

@Composable
fun MischiefNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute, modifier = modifier) {
        composable<HomeRoute> { HomeScreen() }
    }
}
