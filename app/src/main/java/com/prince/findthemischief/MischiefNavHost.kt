package com.prince.findthemischief

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prince.findthemischief.screens.home.HomeScreen
import com.prince.findthemischief.screens.settings.SettingsScreen
import com.prince.findthemischief.screens.setup.SetupScreen
import kotlinx.serialization.Serializable

// Type-safe routes: each destination is a @Serializable object (or data class when it needs arguments).
@Serializable
data object HomeRoute

@Serializable
data object SetupRoute

@Serializable
data object SettingsRoute

@Composable
fun MischiefNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeRoute, modifier = modifier) {
        composable<HomeRoute> {
            HomeScreen(
                onPlay = { navController.navigate(SetupRoute) },
                onSettings = { navController.navigate(SettingsRoute) },
            )
        }
        composable<SetupRoute> {
            SetupScreen(onBack = { navController.popBackStack() })
        }
        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
