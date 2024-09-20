package com.example.db

import org.jetbrains.exposed.sql.Table

object PokemonMovesTable: Table("pokemon_pokemon_moves") {
    val pokemon = integer("pokemonid").references(PokemonTable.id)
    val move = integer("moveid").references(MovesTable.id)
    val level = integer("level")
    override val primaryKey = PrimaryKey(pokemon, move, name = "PK_pokemon_moves")
}