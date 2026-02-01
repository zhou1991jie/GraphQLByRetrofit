package com.example.grapqldemo6.presenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grapqldemo6.domain.usecase.PokemonUseCase
import com.example.grapqldemo6.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.content.Context
import android.util.Log
import com.example.grapqldemo6.R
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val pokemonUseCase: PokemonUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _inputError = MutableStateFlow<String?>(null)
    val inputError: StateFlow<String?> = _inputError.asStateFlow()

    private val _state = MutableStateFlow<PokemonState>(PokemonState.Idle)
    val state: StateFlow<PokemonState> = _state.asStateFlow()

    private var currentPage = 0

    // 搜索失败时，确保加载状态至少显示500毫秒
    private suspend fun ensureMinimumLoadingTime(startTime: Long, minimumTimeMs: Long = 500) {
        val elapsedTime = System.currentTimeMillis() - startTime
        if (elapsedTime < minimumTimeMs) {
            delay(minimumTimeMs - elapsedTime)
        }
    }

    fun updateSearchText(text: String) {
        val filteredText = text.filter { Constants.VALID_INPUT_REGEX.matches(it.toString()) }
        _searchText.value = filteredText
        
        // 检测是否输入了非法字符
        val hasInvalidChars = text != filteredText
        
        // 重置输入错误状态
        _inputError.value = if (hasInvalidChars) {
            context.getString(R.string.search_input_error)
        } else {
            null
        }
        
        // 当用户修改搜索文本时，重置为 Idle 状态，避免自动显示未找到结果的提示
        val currentState = _state.value
        if (currentState is PokemonState.Success || currentState is PokemonState.Error) {
            _state.value = PokemonState.Idle
        }
    }

    fun searchPokemon() {
        val searchQuery = _searchText.value.trim()
        if (searchQuery.isEmpty()) {
            _inputError.value = context.getString(R.string.error_empty_input)
            return
        }

        viewModelScope.launch {
            _state.value = PokemonState.Loading
            val startTime = System.currentTimeMillis()
            val result = pokemonUseCase.searchPokemonByName(
                name = searchQuery,
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
                ensureMinimumLoadingTime(startTime)
                val exception = result.exceptionOrNull()
                if (exception != null) {
                    Log.e("PokemonViewModel", "Search pokemon failed: ${exception.message}", exception)
                }
                _state.value = PokemonState.Error(
                    message = context.getString(R.string.error_network)
                )
            }
        }
    }

    fun loadNextPage() {
        val searchQuery = _searchText.value.trim()
        if (searchQuery.isEmpty()) return

        val currentState = _state.value
        if (currentState !is PokemonState.Success || currentState.hasNextPage.not() || currentState.isLoadingMore) return

        viewModelScope.launch {
            _state.value = currentState.copy(isLoadingMore = true)
            val startTime = System.currentTimeMillis()
            val result = pokemonUseCase.loadNextPage(
                name = searchQuery,
                currentPage = currentPage
            )
            if (result.isSuccess) {
                val pokemonData = result.getOrNull()
                val speciesList = pokemonData?.pokemon_v2_pokemonspecies ?: emptyList()
                val isNotEmpty = speciesList.isNotEmpty()
                if (isNotEmpty) {
                    currentPage++
                }
                _state.value = PokemonState.Success(
                    results = if (isNotEmpty) currentState.results + speciesList else currentState.results,
                    hasNextPage = isNotEmpty,
                    hasSearched = true,
                    isNewSearch = false,
                    isLoadingMore = false,
                    loadMoreError = false
                )
            } else {
                ensureMinimumLoadingTime(startTime)
                val exception = result.exceptionOrNull()
                if (exception != null) {
                    Log.e("PokemonViewModel", "Load next page failed: ${exception.message}", exception)
                }
                // 加载更多失败时，保持现有数据，只重置加载状态并显示错误消息
                _state.value = currentState.copy(
                    isLoadingMore = false,
                    loadMoreError = true
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