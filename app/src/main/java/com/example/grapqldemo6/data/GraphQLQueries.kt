package com.example.grapqldemo6.data

object GraphQLQueries {
//    val SEARCH_POKEMON_SPECIES = """
//        query searchPokemonSpecies(
//        ${'$'}name: String,
//        ${'$'}limit: Int,
//        ${'$'}offset: Int ,
//        ) {
//            pokemon_v2_pokemonspecies(
//            where: {name: {_ilike: ${'$'}name}},
//             limit: ${'$'}limit,
//             offset: ${'$'}offset,
//             ) {
//                id
//                name
//                capture_rate
//                pokemon_v2_pokemoncolor {
//                    id
//                    name
//                }
//                pokemon_v2_pokemons {
//                    id
//                    name
//                    pokemon_v2_pokemonabilities {
//                        id
//                        pokemon_v2_ability {
//                            name
//                        }
//                    }
//                }
//            }
//        }
//    """.trimIndent()


    val SEARCH_POKEMON_SPECIES = """
        query searchPokemonSpecies(
        ${'$'}name: String,
        ${'$'}limit: Int,
        ${'$'}offset: Int,
        ${'$'}order_by: String
        ) {
            pokemon_v2_pokemonspecies(
             where: {name: {_ilike: ${'$'}name}},
             limit: ${'$'}limit,
             offset: ${'$'}offset,
             order_by:{capture_rate: asc}
             ) {
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


    val SEARCH_POKEMON_SPECIES_DESC = """
        query searchPokemonSpecies(
        ${'$'}name: String,
        ${'$'}limit: Int,
        ${'$'}offset: Int,
        ${'$'}order_by: String
        ) {
            pokemon_v2_pokemonspecies(
             where: {name: {_ilike: ${'$'}name}},
             limit: ${'$'}limit,
             offset: ${'$'}offset,
             order_by:{capture_rate: desc}
             ) {
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
}


//val SEARCH_POKEMON_SPECIES = """
//        query searchPokemonSpecies(
//        ${'$'}name: String,
//        ${'$'}limit: Int,
//        ${'$'}offset: Int ,
//        ${'$'}order_by: String
//        ) {
//            pokemon_v2_pokemonspecies(
//            where: {name: {_ilike: ${'$'}name}},
//             limit: ${'$'}limit,
//             offset: ${'$'}offset,
//             where: {order_by:{capture_rate: ${'$'}order_by}}
//             ) {
//                id
//                name
//                capture_rate
//                pokemon_v2_pokemoncolor {
//                    id
//                    name
//                }
//                pokemon_v2_pokemons {
//                    id
//                    name
//                    pokemon_v2_pokemonabilities {
//                        id
//                        pokemon_v2_ability {
//                            name
//                        }
//                    }
//                }
//            }
//        }
//    """.trimIndent()

//https://studio.apollographql.com/sandbox/explorer
//https://beta.pokeapi.co/graphql/v1beta/

//"orderBy":{
//    "capture_rate":"desc" 倒序
//}

//"orderBy":{
//    "capture_rate":"asc" 正序
//}

//{
//    "limit": 2,
//    "offset": null,
//    "where": {
//    "name": {
//    "_ilike": "%-m%",
//}
//},
//    "orderBy":{
//    "capture_rate":"asc"
//}
//}


//query ExampleQuery(
//$offset: Int
//$where: pokemon_v2_pokemonspecies_bool_exp
//$orderBy: [pokemon_v2_pokemonspecies_order_by!]
//$limit: Int
//) {
//    pokemon_v2_pokemonspecies(
//        offset: $offset
//    where: $where
//    order_by: $orderBy
//    limit: $limit
//    ) {
//        id
//        name
//        capture_rate
//        pokemon_v2_pokemoncolor {
//            id
//            name
//        }
//        pokemon_v2_pokemons {
//            id
//            name
//            pokemon_v2_pokemonabilities {
//                id
//                pokemon_v2_ability {
//                    name
//                }
//            }
//        }
//    }
//}
