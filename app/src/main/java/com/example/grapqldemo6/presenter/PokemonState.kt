package com.example.grapqldemo6.presenter

import com.example.grapqldemo6.data.model.PokemonSpecies

sealed class PokemonState {
    object Idle : PokemonState()
    object Loading : PokemonState()
    data class Success(
        val results: List<PokemonSpecies>,
        val hasNextPage: Boolean,
        val hasSearched: Boolean,
        val isNewSearch: Boolean = true,
        val isLoadingMore: Boolean = false,
        val loadMoreError: Boolean = false
    ) : PokemonState()
    data class Error(val message: String) : PokemonState()
}