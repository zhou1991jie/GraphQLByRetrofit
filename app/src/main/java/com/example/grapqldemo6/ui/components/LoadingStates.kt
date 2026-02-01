package com.example.grapqldemo6.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.grapqldemo6.R
import com.example.grapqldemo6.ui.theme.Dimens

@Composable
fun FullScreenLoading() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White.copy(alpha = 0f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(Dimens.iconLarge),
                strokeWidth = Dimens.progressStrokeLarge
            )
            Spacer(modifier = Modifier.height(Dimens.spacingLarge))
            Text(
                text = stringResource(R.string.searching),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun ErrorState(message: String, onRetry: (() -> Unit)? = null) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = Color.Red,
                style = MaterialTheme.typography.bodyLarge
            )
            onRetry?.let {
                Spacer(modifier = Modifier.height(Dimens.spacingMedium))
                androidx.compose.material3.TextButton(
                    onClick = it
                ) {
                    Text(
                        text = stringResource(R.string.tap_to_retry),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.search_pokemon), style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun NoResultsState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.no_results), style = MaterialTheme.typography.titleLarge)
    }
}
