package com.example.grapqldemo6.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class PokemonRepository {

    private val pageSize = 20

    suspend fun searchPokemonByName(name: String, page: Int = 0): Result<PokemonData> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. 构建 GraphQL 查询
                val query = """
                    query searchPokemonSpecies(${'$'}name: String, ${'$'}limit: Int, ${'$'}offset: Int) {
                        pokemon_v2_pokemonspecies(where: {name: {_ilike: ${'$'}name}}, limit: ${'$'}limit, offset: ${'$'}offset) {
                            id
                            name
                            capture_rate
                            pokemon_v2_pokemoncolor {
                                id
                                name
                            }
                            pokemon_v2_pokemons {
                                id
                                name
                                pokemon_v2_pokemonabilities {
                                    id
                                    pokemon_v2_ability {
                                        name
                                    }
                                }
                            }
                        }
                    }
                """.trimIndent()

                // 2. 设置查询变量（支持模糊搜索和分页）
                val variables = mapOf(
                    "name" to "%$name%",
                    "limit" to pageSize,
                    "offset" to page * pageSize
                )

                // 3. 创建请求体
                val request = GraphQLRequest(
                    query = query,
                    variables = variables
                )

                // 4. 发送请求
                val response: Response<GraphQLResponse<PokemonData>> =
                    ApiClient.pokemonService.searchPokemon(request)

                // 5. 处理响应
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.errors.isNullOrEmpty()) {
                        Result.success(body?.data ?: PokemonData(emptyList()))
                    } else {
                        val errorMessage = body?.errors?.joinToString { it.message }
                            ?: "GraphQL error"
                        Result.failure(Exception(errorMessage))
                    }
                } else {
                    Result.failure(Exception("HTTP error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}