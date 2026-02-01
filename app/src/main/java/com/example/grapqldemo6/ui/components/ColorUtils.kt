package com.example.grapqldemo6.ui.components

import androidx.compose.ui.graphics.Color
import com.example.grapqldemo6.ui.theme.*

fun getColorFromName(colorName: String): Color {
    return when (colorName) {
        "red" -> PokemonRed
        "blue" -> PokemonBlue
        "green" -> PokemonGreen
        "yellow" -> PokemonYellow
        "purple" -> PokemonPurple
        "pink" -> PokemonPink
        "brown" -> PokemonBrown
        "gray" -> PokemonGray
        "black" -> PokemonBlack
        "white" -> PokemonDefault
        else -> PokemonDefault
    }
}

fun getContrastColor(backgroundColor: Color): Color {
    val brightness = calculateBrightness(backgroundColor)
    return if (brightness > 0.5) {
        Color.Black
    } else {
        Color.White
    }
}

private fun calculateBrightness(color: Color): Float {
    return (0.299f * color.red + 0.587f * color.green + 0.114f * color.blue)
}
