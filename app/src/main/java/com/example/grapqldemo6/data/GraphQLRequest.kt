package com.example.grapqldemo6.data

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any>? = null
)