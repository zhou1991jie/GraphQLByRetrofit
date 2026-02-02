package com.example.grapqldemo6.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import com.example.grapqldemo6.R
import com.example.grapqldemo6.data.model.Pokemon
import com.example.grapqldemo6.ui.components.getContrastColor
import com.example.grapqldemo6.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    pokemon: Pokemon,
    color: Color,
    onBack: () -> Unit
) {
    val textColor = getContrastColor(color)
    
    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detail_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.nav_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Dimens.spacingLarge, vertical = Dimens.spacingLarge)
        ) {
            Text(
                text = pokemon.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = Dimens.spacingLarge)
            )

            AbilitySectionTitle()

            Spacer(modifier = Modifier.height(Dimens.spacingMedium))

            LazyColumn {
                items(pokemon.pokemon_v2_pokemonabilities ?: emptyList()) { ability ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.spacingExtraSmall),
                        colors = CardDefaults.cardColors(containerColor = color)
                    ) {
                        Text(
                            text = ability.pokemon_v2_ability.name ?: stringResource(R.string.unknown_ability),
                            color = textColor,
                            modifier = Modifier.padding(Dimens.spacingLarge)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AbilitySectionTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.borderRadiusSmall))
            .background(Color(0xFF1976D2))
            .padding(Dimens.spacingMedium)
    ) {
        Text(
            text = stringResource(R.string.abilities_list),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}