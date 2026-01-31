package com.example.grapqldemo6.data.model

import java.io.Serializable

data class Pokemon(
    val id: Int,
    val name: String,
    val pokemon_v2_pokemonabilities: List<PokemonAbility>?
) : Serializable