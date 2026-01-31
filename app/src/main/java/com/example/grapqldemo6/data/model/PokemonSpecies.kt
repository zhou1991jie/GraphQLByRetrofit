package com.example.grapqldemo6.data.model

import java.io.Serializable

data class PokemonSpecies(
    val id: Int,
    val name: String,
    val capture_rate: Int?,
    val pokemon_v2_pokemoncolor: PokemonColor?,
    val pokemon_v2_pokemons: List<Pokemon>?
) : Serializable