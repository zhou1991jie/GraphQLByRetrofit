package com.example.grapqldemo6.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.clickable
import com.example.grapqldemo6.R
import com.example.grapqldemo6.data.model.Pokemon
import com.example.grapqldemo6.presenter.PokemonState
import androidx.compose.ui.graphics.Color
import com.example.grapqldemo6.ui.theme.Dimens
import com.example.grapqldemo6.util.isFalse

@Composable
fun PokemonList(
    state: PokemonState.Success,
    onPokemonClick: (pokemon: Pokemon, color: Color) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Dimens.listContentPadding)
    ) {
        items(state.results) { species ->
            PokemonSpeciesItem(
                species = species,
                onPokemonClick = onPokemonClick
            )
        }

        item {
            LoadMoreItem(
                hasNextPage = state.hasNextPage,
                isLoadingMore = state.isLoadingMore,
                loadMoreError = state.loadMoreError,
                onLoadMore = onLoadMore
            )
        }
    }
}

@Composable
fun LoadMoreItem(
    hasNextPage: Boolean,
    isLoadingMore: Boolean,
    loadMoreError: Boolean,
    onLoadMore: () -> Unit
) {
    if (hasNextPage && isLoadingMore.isFalse() && loadMoreError.isFalse()) {
        LaunchedEffect(Unit) {
            onLoadMore()
        }
    }

    if (isLoadingMore) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLarge),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(Dimens.iconMedium),
                    strokeWidth = Dimens.progressStrokeSmall
                )
                Spacer(modifier = Modifier.height(Dimens.spacingMedium))
                Text(
                    text = stringResource(R.string.loading_more),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (loadMoreError) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLarge),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.error_network),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = Dimens.spacingMedium)
                )
                Text(
                    text = stringResource(R.string.tap_to_retry),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onLoadMore()
                    }
                )
            }
        }
    }
}
