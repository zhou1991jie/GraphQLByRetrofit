package com.example.grapqldemo6.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.grapqldemo6.R
import com.example.grapqldemo6.data.model.Pokemon
import com.example.grapqldemo6.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    pokemon: Pokemon,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(pokemon.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Dimens.spacingLarge)
        ) {
            Text(
                text = pokemon.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = Dimens.spacingLarge)
            )

            Text(stringResource(R.string.abilities_list), style = MaterialTheme.typography.bodyLarge)

            LazyColumn {
                items(pokemon.pokemon_v2_pokemonabilities ?: emptyList()) { ability ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spacingExtraSmall)
                    ) {
                        Text(
                            text = ability.pokemon_v2_ability.name ?: stringResource(R.string.unknown_ability),
                            modifier = Modifier.padding(Dimens.spacingLarge)
                        )
                    }
                }
            }
        }
    }
}