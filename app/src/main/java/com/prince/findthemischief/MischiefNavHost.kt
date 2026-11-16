package com.prince.findthemischief

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
            val context = LocalContext.current
            SetupScreen(
                onBack = { navController.popBackStack() },
                // Phase 5 replaces this with navigation to the first Pass screen.
                onBegin = {
                    Toast.makeText(context, R.string.setup_begin_not_ready, Toast.LENGTH_SHORT).show()
                },
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
