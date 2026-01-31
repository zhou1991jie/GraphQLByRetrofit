package com.example.grapqldemo6.data.model

import java.io.Serializable

// 顶层查询结果数据模型
data class PokemonData(
    val pokemon_v2_pokemonspecies: List<PokemonSpecies>
) : Serializable