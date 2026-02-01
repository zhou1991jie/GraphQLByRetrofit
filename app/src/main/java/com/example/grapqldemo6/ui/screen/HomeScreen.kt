package com.example.grapqldemo6.ui.screen

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.grapqldemo6.data.model.Pokemon
import com.example.grapqldemo6.presenter.PokemonState
import com.example.grapqldemo6.presenter.PokemonViewModel
import com.example.grapqldemo6.ui.components.EmptyState
import com.example.grapqldemo6.ui.components.ErrorState
import com.example.grapqldemo6.ui.components.FullScreenLoading
import com.example.grapqldemo6.ui.components.NoResultsState
import com.example.grapqldemo6.ui.components.PokemonList
import com.example.grapqldemo6.ui.components.SearchBar
import com.example.grapqldemo6.ui.components.SearchErrorText
import com.example.grapqldemo6.ui.theme.Dimens
import com.example.grapqldemo6.util.isNull

@Composable
fun HomeScreen(
    onPokemonClick: (pokemon: Pokemon, color: Color) -> Unit,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val searchText by viewModel.searchText.collectAsState()
    val inputError by viewModel.inputError.collectAsState()
    val state by viewModel.state.collectAsState()

    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    )
                }
                .padding(
                    top = Dimens.spacingXLarge,
                    start = Dimens.spacingLarge,
                    end = Dimens.spacingLarge,
                    bottom = Dimens.spacingLarge
                )
        ) {
            SearchBar(
                searchText = searchText,
                isInputValid = inputError.isNull(),
                isLoading = state is PokemonState.Loading,
                onSearchTextChange = { viewModel.updateSearchText(it) },
                onSearchClick = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    viewModel.searchPokemon()
                },
                modifier = Modifier.focusRequester(focusRequester)
            )

            SearchErrorText(
                errorMessage = inputError,
                modifier = Modifier.padding(
                    top = Dimens.spacingExtraSmall,
                    start = Dimens.spacingMedium,
                    end = Dimens.spacingMedium
                )
            )

            Spacer(modifier = Modifier.height(Dimens.spacingLarge))

            ContentArea(
                state = state,
                searchText = searchText,
                listState = listState,
                onPokemonClick = onPokemonClick,
                onLoadMore = { viewModel.loadNextPage() },
                viewModel = viewModel
            )
        }

        if (state is PokemonState.Loading) {
            FullScreenLoading()
        }
    }
}

@Composable
private fun ContentArea(
    state: PokemonState,
    searchText: String,
    listState: LazyListState,
    onPokemonClick: (pokemon: Pokemon, color: Color) -> Unit,
    onLoadMore: () -> Unit,
    viewModel: PokemonViewModel
) {
    when (state) {
        is PokemonState.Error -> {
            // 搜索文本不为空的情况下（确定已经搜索过），才展示重试按钮
            val showRetryButton = searchText.trim().isNotEmpty()
            ErrorState(
                message = state.message,
                onRetry = if (showRetryButton) viewModel::searchPokemon else null
            )
            Spacer(modifier = Modifier.height(Dimens.spacingLarge))
        }

        is PokemonState.Success -> {
            if (state.results.isNotEmpty()) {
                LaunchedEffect(state.results.size) {
                    if (state.isNewSearch) {
                        listState.animateScrollToItem(0)
                    }
                }

                PokemonList(
                    state = state,
                    onPokemonClick = onPokemonClick,
                    onLoadMore = onLoadMore
                )
            } else if (state.hasSearched && searchText.isNotBlank()) {
                NoResultsState()
            }
        }

        is PokemonState.Idle -> {
            EmptyState()
        }

        is PokemonState.Loading -> {
        }
    }
}
