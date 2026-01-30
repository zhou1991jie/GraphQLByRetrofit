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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.grapqldemo6.data.Pokemon
import com.example.grapqldemo6.data.PokemonSpecies
import com.example.grapqldemo6.presenter.PokemonViewModel


@Composable
fun HomeScreen(
    onPokemonClick: (pokemon: Pokemon) -> Unit,
    viewModel: PokemonViewModel = hiltViewModel()
) {
    val searchText = viewModel.searchText.collectAsState()
    val isLoading = viewModel.isLoading.collectAsState()
    val searchResults = viewModel.searchResults.collectAsState()
    val hasNextPage = viewModel.hasNextPage.collectAsState()
    val errorMessage = viewModel.errorMessage.collectAsState()
    val hasSearched = viewModel.hasSearched.collectAsState()
    
    // 记住列表状态
    val listState = rememberLazyListState()
    
    // 当搜索结果变化时，平滑滚动到顶部
    LaunchedEffect(searchResults.value) {
        if (searchResults.value.isNotEmpty()) {
            listState.animateScrollToItem(0)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
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

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.searchPokemon() },
                    enabled = searchText.value.isNotBlank() && !isLoading.value,
                    modifier = Modifier.height(56.dp)
                ) {
                    if (isLoading.value) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("搜索")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 错误信息
            errorMessage.value?.let {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 搜索结果列表
            if (searchResults.value.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(searchResults.value) { species ->
                        PokemonSpeciesItem(
                            species = species,
                            onPokemonClick = onPokemonClick
                        )
                    }

                    // 分页加载指示器
                    item {
                        if (hasNextPage.value) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }

                            LaunchedEffect(Unit) {
                                viewModel.loadNextPage()
                            }
                        }
                    }
                }
            } else if (hasSearched.value && searchText.value.isNotBlank() && !isLoading.value && errorMessage.value == null) {
                // 空状态 - 搜索后未找到结果
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("未找到相关宝可梦", style = MaterialTheme.typography.titleLarge)
                }
            } else if (!hasSearched.value) {
                // 默认空白状态 - 提示用户搜索
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("搜索更多宝可梦", style = MaterialTheme.typography.titleLarge)
                }
            }
        }

        // 全屏加载动画
        if (isLoading.value && searchResults.value.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color.White.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
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
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.Red)
                                .padding(8.dp, 4.dp)
                        ) {
                            Text(
                                text = "捕获率: ${species.capture_rate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1976D2))
                            .padding(8.dp)
                    ) {
                        Text("包含的宝可梦:", 
                            style = MaterialTheme.typography.titleMedium, 
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        species.pokemon_v2_pokemons?.forEachIndexed { index, pokemon ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(color)
                                    .clickable {
                                        onPokemonClick(pokemon)
                                    }
                                    .padding(6.dp)
                            ) {
                                Text(
                                    text = pokemon.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
        }
    }
}

@Composable
fun getColorFromName(colorName: String): Color {
    return when (colorName) {
        "red" -> Color.Red
        "blue" -> Color.Blue
        "green" -> Color.Green
        "yellow" -> Color.Yellow
        "purple" -> Color(0xFF9C27B0)
        "pink" -> Color(0xFFE91E63)
        "brown" -> Color(0xFF795548)
        "gray" -> Color.Gray
        "black" -> Color.Black
        "white" -> Color(0xFF757575)
        else -> Color(0xFF757575)
    }
}