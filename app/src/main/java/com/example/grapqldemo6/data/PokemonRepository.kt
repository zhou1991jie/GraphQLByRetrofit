package com.example.grapqldemo6.data

import com.example.grapqldemo6.data.model.PokemonData
import com.example.grapqldemo6.domain.PokemonApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val pokemonApiService: PokemonApiService
) {


    suspend fun searchPokemonByName(
        name: String,
        page: Int = 0,
        orderBy: String
    ): Result<PokemonData> {
        return withContext(Dispatchers.IO) {
            try {
                val query = if(orderBy == ApiConstants.PAGE_ASC)GraphQLQueries.SEARCH_POKEMON_SPECIES else GraphQLQueries.SEARCH_POKEMON_SPECIES_DESC
                val variables = mapOf(
                    "name" to "%$name%",
                    "limit" to ApiConstants.PAGE_SIZE,
                    "offset" to page * ApiConstants.PAGE_SIZE,
//                    "orderBy" to orderBy
//                    "orderBy" to ApiConstants.PAGE_ASC
                )

                val request = GraphQLRequest(
                    query = query,
                    variables = variables
                )

                val response: Response<GraphQLResponse<PokemonData>> =
                    pokemonApiService.searchPokemon(request)

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