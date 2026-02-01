package com.example.grapqldemo6.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.grapqldemo6.data.model.PokemonWithColor
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
                onPokemonClick = { pokemon, color ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "pokemonWithColor",
                        PokemonWithColor(pokemon, color)
                    )
                    navController.navigate("detail")
                }
            )
        }

        composable("detail") {
            val pokemonWithColor = navController.previousBackStackEntry?.savedStateHandle?.get<PokemonWithColor>("pokemonWithColor")
            pokemonWithColor?.let {
                DetailScreen(
                    pokemon = it.pokemon,
                    color = it.color,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

