package com.example.grapqldemo6.data.model

import java.io.Serializable

data class PokemonAbility(
    val id: Int,
    val pokemon_v2_ability: Ability
) : Serializable