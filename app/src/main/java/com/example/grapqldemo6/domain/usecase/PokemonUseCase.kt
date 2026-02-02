package com.example.grapqldemo6.domain.usecase

import com.example.grapqldemo6.data.PokemonRepository
import com.example.grapqldemo6.data.model.PokemonData
import javax.inject.Inject

class PokemonUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    suspend fun searchPokemonByName(name: String, page: Int = 0): Result<PokemonData> {
        return pokemonRepository.searchPokemonByName(name, page)
    }

    suspend fun loadNextPage(name: String, currentPage: Int): Result<PokemonData> {
        return pokemonRepository.searchPokemonByName(name, currentPage + 1)
    }
}