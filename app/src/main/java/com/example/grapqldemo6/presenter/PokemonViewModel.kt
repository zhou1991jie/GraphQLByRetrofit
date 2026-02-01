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
import android.content.Context
import com.example.grapqldemo6.R
import javax.inject.Inject

@HiltViewModel
class PokemonViewModel @Inject constructor(
    private val pokemonUseCase: PokemonUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private val _hasInvalidInput = MutableStateFlow(false)
    val hasInvalidInput: StateFlow<Boolean> = _hasInvalidInput.asStateFlow()

    private val _state = MutableStateFlow<PokemonState>(PokemonState.Idle)
    val state: StateFlow<PokemonState> = _state.asStateFlow()

    private var currentPage = 0

    fun updateSearchText(text: String) {
        // 使用常量类中的正则表达式过滤非法字符，只保留字母和中划线
        val filteredText = text.filter { Constants.VALID_INPUT_REGEX.matches(it.toString()) }
        _searchText.value = filteredText
        
        // 检测是否输入了非法字符
        val hasInvalidChars = text != filteredText
        _hasInvalidInput.value = hasInvalidChars
        
        // 当用户修改搜索文本时，重置为 Idle 状态，避免自动显示未找到结果的提示
        val currentState = _state.value
        if (currentState is PokemonState.Success || currentState is PokemonState.Error) {
            _state.value = PokemonState.Idle
        }
    }

    fun searchPokemon() {
        val searchQuery = _searchText.value.trim()
        if (searchQuery.isEmpty()) {
            _state.value = PokemonState.Error(
                message = context.getString(R.string.error_empty_input)
            )
            return
        }

        viewModelScope.launch {
            _state.value = PokemonState.Loading
            try {
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
                    _state.value = PokemonState.Error(
                        message = result.exceptionOrNull()?.message ?: context.getString(R.string.error_search_failed)
                    )
                }
            } catch (e: Exception) {
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
            try {
                val result = pokemonUseCase.loadNextPage(
                    name = searchQuery,
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
                        message = result.exceptionOrNull()?.message ?: context.getString(R.string.error_load_more_failed)
                    )
                }
            } catch (e: Exception) {
                _state.value = PokemonState.Error(
                    message = context.getString(R.string.error_network)
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