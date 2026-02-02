package com.example.grapqldemo6.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import com.example.grapqldemo6.R
import com.example.grapqldemo6.data.model.Pokemon
import com.example.grapqldemo6.data.model.PokemonSpecies
import com.example.grapqldemo6.ui.theme.Dimens

@Composable
fun PokemonSpeciesItem(
    species: PokemonSpecies,
    onPokemonClick: (pokemon: Pokemon, color: Color) -> Unit
) {
    val color = getColorFromName(species.pokemon_v2_pokemoncolor?.name ?: "white")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimens.spacingMedium),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingLarge)
        ) {
            SpeciesHeader(
            name = species.name,
            captureRate = species.capture_rate ?: 0
        )
            
            Spacer(modifier = Modifier.height(Dimens.spacingLarge))
            
            PokemonSectionTitle()
            
            Spacer(modifier = Modifier.height(Dimens.spacingMedium))
            
            PokemonList(
                pokemons = species.pokemon_v2_pokemons ?: emptyList(),
                color = color,
                onPokemonClick = onPokemonClick
            )
        }
    }
}

@Composable
private fun SpeciesHeader(
    name: String,
    captureRate: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        CaptureRateBadge(captureRate = captureRate)
    }
}

@Composable
private fun CaptureRateBadge(captureRate: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.borderRadiusMedium))
            .background(Color.Red)
            .padding(Dimens.spacingMedium, Dimens.spacingExtraSmall)
    ) {
        Text(
            text = stringResource(R.string.capture_rate, captureRate),
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PokemonSectionTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.borderRadiusSmall))
            .background(Color(0xFF1976D2))
            .padding(Dimens.spacingMedium)
    ) {
        Text(
            text = stringResource(R.string.pokemon_list),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PokemonList(
    pokemons: List<Pokemon>,
    color: Color,
    onPokemonClick: (pokemon: Pokemon, color: Color) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.listSpacing),
        verticalArrangement = Arrangement.spacedBy(Dimens.listSpacing)
    ) {
        pokemons.forEach { pokemon ->
            PokemonChip(
                name = pokemon.name,
                color = color,
                onClick = { onPokemonClick(pokemon, color) }
            )
        }
    }
}

@Composable
private fun PokemonChip(
    name: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.borderRadiusSmall))
            .background(color)
            .clickable { onClick() }
            .padding(Dimens.spacingSmall)
    ) {
        TextWithStroke(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            backgroundColor = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TextWithStroke(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    textColor: Color = Color.Black,
    backgroundColor: Color = Color.Transparent,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val displayColor = if (backgroundColor != Color.Transparent) {
        getContrastColor(backgroundColor)
    } else {
        textColor
    }
    
    Text(
        text = text,
        style = style.copy(color = displayColor),
        maxLines = maxLines,
        overflow = overflow
    )
}
