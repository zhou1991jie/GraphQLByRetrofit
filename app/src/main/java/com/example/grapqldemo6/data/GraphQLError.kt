package com.example.grapqldemo6.data

data class GraphQLError(
    val message: String,
    val locations: List<Location>? = null
)