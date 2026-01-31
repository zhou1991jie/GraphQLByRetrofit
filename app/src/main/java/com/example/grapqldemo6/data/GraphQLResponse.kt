package com.example.grapqldemo6.data

data class GraphQLResponse<T>(
    val data: T? = null,
    val errors: List<GraphQLError>? = null
)