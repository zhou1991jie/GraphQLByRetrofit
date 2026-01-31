package com.example.grapqldemo6.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.grapqldemo6.data.model.Pokemon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.grapqldemo6.util.PreferenceManager



@Composable
fun PokemonApp(
    preferenceManager: PreferenceManager
) {
    val navController = rememberNavController()
    var isFirstLaunch by rememberSaveable { mutableStateOf(preferenceManager.isFirstLaunch()) }

    NavHost(navController = navController, startDestination = if (isFirstLaunch) "welcome" else "home") {
        composable("welcome") {
            WelcomeScreen {
                preferenceManager.setFirstLaunchCompleted()
                navController.navigate("home") {
                    popUpTo("welcome") { inclusive = true }
                }
            }
        }

        composable("home") {
            HomeScreen(
                onPokemonClick = { pokemon ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("pokemon", pokemon)
                    navController.navigate("detail")
                }
            )
        }

        composable("detail") {
            val pokemon = navController.previousBackStackEntry?.savedStateHandle?.get<Pokemon>("pokemon")
            pokemon?.let {
                DetailScreen(
                    pokemon = it,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

