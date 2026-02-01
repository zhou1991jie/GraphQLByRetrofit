package com.example.grapqldemo6.data.model

import androidx.compose.ui.graphics.Color
import java.io.Serializable

data class PokemonWithColor(
    val pokemon: Pokemon,
    val color: Color
) : Serializable