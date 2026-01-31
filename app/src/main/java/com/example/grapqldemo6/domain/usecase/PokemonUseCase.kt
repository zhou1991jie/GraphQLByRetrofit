package com.example.grapqldemo6.domain.usecase

import com.example.grapqldemo6.data.PokemonRepository
import com.example.grapqldemo6.data.model.PokemonData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PokemonUseCase @Inject constructor(
    private val pokemonRepository: PokemonRepository
) {

    suspend fun searchPokemonByName(name: String, page: Int = 0): Result<PokemonData> {
        return withContext(Dispatchers.IO) {
            pokemonRepository.searchPokemonByName(name, page)
        }
    }

    suspend fun loadNextPage(name: String, currentPage: Int): Result<PokemonData> {
        return withContext(Dispatchers.IO) {
            pokemonRepository.searchPokemonByName(name, currentPage + 1)
        }
    }
}