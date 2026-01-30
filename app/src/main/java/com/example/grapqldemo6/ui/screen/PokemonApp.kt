package com.example.grapqldemo6.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.grapqldemo6.data.Pokemon
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
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

