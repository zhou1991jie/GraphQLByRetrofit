package com.example.grapqldemo6.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.layout.layout
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.grapqldemo6.data.model.Pokemon
import com.example.grapqldemo6.data.model.PokemonSpecies
import com.example.grapqldemo6.presenter.PokemonState
import com.example.grapqldemo6.presenter.PokemonViewModel
import com.example.grapqldemo6.ui.theme.Dimens
import com.example.grapqldemo6.ui.theme.*


@Composable
fun HomeScreen(
    onPokemonClick: (pokemon: Pokemon) -> Unit,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val searchText = viewModel.searchText.collectAsState()
    val state = viewModel.state.collectAsState()
    
    // 记住列表状态
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Dimens.spacingXLarge, start = Dimens.spacingLarge, end = Dimens.spacingLarge, bottom = Dimens.spacingLarge)
        ) {
            // 搜索框和按钮
            Row (
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchText.value,
                    onValueChange = { viewModel.updateSearchText(it) },
                    placeholder = { Text("输入宝可梦名称...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(Dimens.spacingMedium))

                Button(
                    onClick = { viewModel.searchPokemon() },
                    enabled = searchText.value.isNotBlank() && state.value !is PokemonState.Loading,
                    modifier = Modifier.height(Dimens.buttonHeight)
                ) {
                    if (state.value is PokemonState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(Dimens.iconSmall),
                            color = Color.White,
                            strokeWidth = Dimens.progressStrokeSmall
                        )
                    } else {
                        Text("搜索")
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spacingLarge))

            when (val currentState = state.value) {
                is PokemonState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimens.spacingMedium),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentState.message,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Spacer(modifier = Modifier.height(Dimens.spacingLarge))
                }
                is PokemonState.Success -> {
                    if (currentState.results.isNotEmpty()) {
                        // 当搜索结果变化时，平滑滚动到顶部（仅在新搜索时）
                        LaunchedEffect(currentState.results.size) {
                            if (currentState.isNewSearch) {
                                listState.animateScrollToItem(0)
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            contentPadding = PaddingValues(bottom = Dimens.listContentPadding)
                        ) {
                            items(currentState.results) { species ->
                                PokemonSpeciesItem(
                                    species = species,
                                    onPokemonClick = onPokemonClick
                                )
                            }

                            // 加载更多的loading item
                            item {
                                // 当滚动到这个item时，触发加载更多
                                if (currentState.hasNextPage && !currentState.isLoadingMore) {
                                    LaunchedEffect(Unit) {
                                        viewModel.loadNextPage()
                                    }
                                }

                                if (currentState.isLoadingMore) {
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
                                                text = "加载中...",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    } else if (currentState.hasSearched && searchText.value.isNotBlank()) {
                        // 空状态 - 搜索后未找到结果
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("未找到相关宝可梦", style = MaterialTheme.typography.titleLarge)
                        }
                    }
                }
                is PokemonState.Idle -> {
                    // 默认空白状态 - 提示用户搜索
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("搜索更多宝可梦", style = MaterialTheme.typography.titleLarge)
                    }
                }
                is PokemonState.Loading -> {
                    // 加载状态已在下方处理
                }
            }
        }

        // 全屏加载动画
        if (state.value is PokemonState.Loading) {
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
                        text = "搜索中...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun PokemonSpeciesItem(
    species: PokemonSpecies,
    onPokemonClick: (Pokemon) -> Unit
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
            Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = species.name,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                            .clip(RoundedCornerShape(Dimens.borderRadiusMedium))
                            .background(Color.Red)
                            .padding(Dimens.spacingMedium, Dimens.spacingExtraSmall)
                        ) {
                            Text(
                                text = "捕获率: ${species.capture_rate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingLarge))

                    Box(
                        modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Dimens.borderRadiusSmall))
                        .background(Color(0xFF1976D2))
                        .padding(Dimens.spacingMedium)
                    ) {
                        Text("包含的宝可梦:", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spacingMedium))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.listSpacing),
                        verticalArrangement = Arrangement.spacedBy(Dimens.listSpacing)
                    ) {
                        species.pokemon_v2_pokemons?.forEachIndexed { index, pokemon ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(Dimens.borderRadiusSmall))
                                    .background(color)
                                    .clickable {
                                        onPokemonClick(pokemon)
                                    }
                                    .padding(Dimens.spacingSmall)
                            ) {
                                TextWithStroke(
                                    text = pokemon.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textColor = Color.Black,
                                    strokeColor = Color.White,
                                    strokeWidth = 2f,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
        }
    }
}

fun getColorFromName(colorName: String): Color {
    return when (colorName) {
        "red" -> PokemonRed
        "blue" -> PokemonBlue
        "green" -> PokemonGreen
        "yellow" -> PokemonYellow
        "purple" -> PokemonPurple
        "pink" -> PokemonPink
        "brown" -> PokemonBrown
        "gray" -> PokemonGray
        "black" -> PokemonBlack
        "white" -> PokemonDefault
        else -> PokemonDefault
    }
}

@Composable
fun TextWithStroke(
    text: String,
    style: TextStyle,
    textColor: Color = Color.Black,
    strokeColor: Color = Color.White,
    strokeWidth: Float = 2f,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip
) {
    val strokeWidthDp = strokeWidth.dp
    Box {
        // 绘制白色描边
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(-strokeWidthDp.roundToPx(), -strokeWidthDp.roundToPx())
                }
            }
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(-strokeWidthDp.roundToPx(), 0)
                }
            }
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(-strokeWidthDp.roundToPx(), strokeWidthDp.roundToPx())
                }
            }
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(0, -strokeWidthDp.roundToPx())
                }
            }
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(0, strokeWidthDp.roundToPx())
                }
            }
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(strokeWidthDp.roundToPx(), -strokeWidthDp.roundToPx())
                }
            }
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(strokeWidthDp.roundToPx(), 0)
                }
            }
        )
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = strokeColor,
            modifier = Modifier.layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    placeable.place(strokeWidthDp.roundToPx(), strokeWidthDp.roundToPx())
                }
            }
        )
        // 绘制文字本身
        Text(
            text = text,
            style = style,
            maxLines = maxLines,
            overflow = overflow,
            color = textColor
        )
    }
}