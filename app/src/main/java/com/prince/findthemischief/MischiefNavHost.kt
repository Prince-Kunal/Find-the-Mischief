package com.prince.findthemischief

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.prince.findthemischief.game.domain.GameSetup
import com.prince.findthemischief.screens.game.GameScreen
import com.prince.findthemischief.screens.home.HomeScreen
import com.prince.findthemischief.screens.settings.SettingsScreen
import com.prince.findthemischief.screens.setup.SetupScreen
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// Type-safe routes: each destination is a @Serializable object (or data class when it needs arguments).
@Serializable
data object HomeRoute

@Serializable
data object SetupRoute

@Serializable
data object SettingsRoute

/** A running game. The setup travels as JSON so it survives the app being restored. */
@Serializable
data class GameRoute(val setupJson: String)

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
            SetupScreen(
                onBack = { navController.popBackStack() },
                onBegin = { setup -> navController.navigate(GameRoute(Json.encodeToString(setup))) },
            )
        }
        // No slide or fade into or out of a game: a card must never be visible mid-animation.
        composable<GameRoute>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) { entry ->
            val setup = remember(entry) { Json.decodeFromString<GameSetup>(entry.toRoute<GameRoute>().setupJson) }
            GameScreen(setup = setup, onExit = { navController.popBackStack() })
        }
        composable<SettingsRoute> {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
