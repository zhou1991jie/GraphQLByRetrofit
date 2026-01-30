package com.example.grapqldemo6.data

data class GraphQLResponse<T>(
    val data: T? = null,
    val errors: List<GraphQLError>? = null
)

data class GraphQLError(
    val message: String,
    val locations: List<Location>? = null
)

data class Location(
    val line: Int,
    val column: Int
)