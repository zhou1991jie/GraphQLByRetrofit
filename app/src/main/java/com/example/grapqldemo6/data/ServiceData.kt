package com.example.grapqldemo6.data

import java.io.Serializable

// 根据图片中的查询结构定义数据模型
data class PokemonData(
    val pokemon_v2_pokemonspecies: List<PokemonSpecies>
) : Serializable

data class PokemonSpecies(
    val id: Int,
    val name: String,
    val capture_rate: Int?,
    val pokemon_v2_pokemoncolor: PokemonColor?,
    val pokemon_v2_pokemons: List<Pokemon>?
) : Serializable

data class PokemonColor(
    val id: Int,
    val name: String
) : Serializable

data class Pokemon(
    val id: Int,
    val name: String,
    val pokemon_v2_pokemonabilities: List<PokemonAbility>?
) : Serializable

data class PokemonAbility(
    val id: Int,
    val pokemon_v2_ability: Ability
) : Serializable

data class Ability(
    val name: String
) : Serializable