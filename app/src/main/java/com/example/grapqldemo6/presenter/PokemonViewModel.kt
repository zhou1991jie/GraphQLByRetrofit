package com.example.grapqldemo6.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grapqldemo6.domain.usecase.PokemonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val pokemonUseCase: PokemonUseCase
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _state = MutableStateFlow<PokemonState>(PokemonState.Idle)
    val state: StateFlow<PokemonState> = _state.asStateFlow()

    private var currentPage = 0

    fun updateSearchText(text: String) {
        _searchText.value = text
        // 当用户修改搜索文本时，重置为 Idle 状态，避免自动显示未找到结果的提示
        val currentState = _state.value
        if (currentState is PokemonState.Success) {
            _state.value = PokemonState.Idle
        }
    }

    fun searchPokemon() {
        viewModelScope.launch {
            _state.value = PokemonState.Loading
            try {
                val result = pokemonUseCase.searchPokemonByName(
                    name = _searchText.value,
                    page = 0
                )
                if (result.isSuccess) {
                    val pokemonData = result.getOrNull()
                    val speciesList = pokemonData?.pokemon_v2_pokemonspecies ?: emptyList()
                    currentPage = 0
                    _state.value = PokemonState.Success(
                        results = speciesList,
                        hasNextPage = speciesList.isNotEmpty(),
                        hasSearched = true
                    )
                } else {
                    _state.value = PokemonState.Error(
                        message = result.exceptionOrNull()?.message ?: "搜索失败"
                    )
                }
            } catch (e: Exception) {
                _state.value = PokemonState.Error(
                    message = "网络错误，请稍后重试"
                )
            }
        }
    }

    fun loadNextPage() {
        val currentState = _state.value
        if (currentState !is PokemonState.Success || currentState.hasNextPage.not() || currentState.isLoadingMore) return

        viewModelScope.launch {
            _state.value = currentState.copy(isLoadingMore = true)
            try {
                val result = pokemonUseCase.loadNextPage(
                    name = _searchText.value,
                    currentPage = currentPage
                )
                if (result.isSuccess) {
                    val pokemonData = result.getOrNull()
                    val speciesList = pokemonData?.pokemon_v2_pokemonspecies ?: emptyList()
                    if (speciesList.isNotEmpty()) {
                        currentPage++
                        _state.value = PokemonState.Success(
                            results = currentState.results + speciesList,
                            hasNextPage = true,
                            hasSearched = true,
                            isNewSearch = false,
                            isLoadingMore = false
                        )
                    } else {
                        _state.value = PokemonState.Success(
                            results = currentState.results,
                            hasNextPage = false,
                            hasSearched = true,
                            isNewSearch = false,
                            isLoadingMore = false
                        )
                    }
                } else {
                    _state.value = PokemonState.Error(
                        message = result.exceptionOrNull()?.message ?: "加载更多失败"
                    )
                }
            } catch (e: Exception) {
                _state.value = PokemonState.Error(
                    message = "网络错误，请稍后重试"
                )
            }
        }
    }

    fun clearError() {
        val currentState = _state.value
        if (currentState is PokemonState.Error) {
            _state.value = PokemonState.Idle
        }
    }
}