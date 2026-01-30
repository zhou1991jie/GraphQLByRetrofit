package com.example.grapqldemo6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.grapqldemo6.ui.screen.PokemonApp
import com.example.grapqldemo6.ui.theme.GrapqlDemo6Theme
import com.example.grapqldemo6.util.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GrapqlDemo6Theme {
                PokemonApp(preferenceManager = preferenceManager)
            }
        }
    }
}

