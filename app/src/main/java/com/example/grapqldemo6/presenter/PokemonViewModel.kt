package com.example.grapqldemo6.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grapqldemo6.data.PokemonRepository
import com.example.grapqldemo6.data.PokemonSpecies
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val pokemonRepository: PokemonRepository
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PokemonSpecies>>(emptyList())
    val searchResults: StateFlow<List<PokemonSpecies>> = _searchResults.asStateFlow()

    private val _hasNextPage = MutableStateFlow(true)
    val hasNextPage: StateFlow<Boolean> = _hasNextPage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private var currentPage = 0

    fun updateSearchText(text: String) {
        _searchText.value = text
        _errorMessage.value = null
        _hasSearched.value = false
    }

    fun searchPokemon() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _hasSearched.value = true
            try {
                val result = pokemonRepository.searchPokemonByName(
                    name = _searchText.value,
                    page = 0
                )
                if (result.isSuccess) {
                    val pokemonData = result.getOrNull()
                    val speciesList = pokemonData?.pokemon_v2_pokemonspecies ?: emptyList()
                    _searchResults.value = speciesList
                    currentPage = 0
                    _hasNextPage.value = speciesList.isNotEmpty()
                } else {
                    // 处理错误
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "搜索失败"
                    _searchResults.value = emptyList()
                    _hasNextPage.value = false
                }
            } catch (e: Exception) {
                // 处理错误
                _errorMessage.value = "网络错误，请稍后重试"
                _searchResults.value = emptyList()
                _hasNextPage.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadNextPage() {
        if (!_hasNextPage.value || _isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val nextPage = currentPage + 1
                val result = pokemonRepository.searchPokemonByName(
                    name = _searchText.value,
                    page = nextPage
                )
                if (result.isSuccess) {
                    val pokemonData = result.getOrNull()
                    val speciesList = pokemonData?.pokemon_v2_pokemonspecies ?: emptyList()
                    if (speciesList.isNotEmpty()) {
                        _searchResults.value = _searchResults.value + speciesList
                        currentPage = nextPage
                    } else {
                        _hasNextPage.value = false
                    }
                } else {
                    // 处理错误
                    _errorMessage.value = result.exceptionOrNull()?.message ?: "加载更多失败"
                    _hasNextPage.value = false
                }
            } catch (e: Exception) {
                // 处理错误
                _errorMessage.value = "网络错误，请稍后重试"
                _hasNextPage.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}