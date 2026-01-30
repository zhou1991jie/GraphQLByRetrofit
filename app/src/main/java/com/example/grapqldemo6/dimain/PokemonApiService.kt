package com.example.grapqldemo6.dimain

import com.example.grapqldemo6.data.GraphQLRequest
import com.example.grapqldemo6.data.GraphQLResponse
import com.example.grapqldemo6.data.PokemonData
import retrofit2.http.Body
import retrofit2.http.POST

interface PokemonApiService {
    @POST(".")
    suspend fun searchPokemon(@Body request: GraphQLRequest): retrofit2.Response<GraphQLResponse<PokemonData>>
}
