package com.example.db

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EvolutionTable: Table("evolution") {
    val evolveFrom = integer("evolvefrom").references(PokemonTable.id, onDelete = ReferenceOption.CASCADE)
    val evolveTo = integer("evolveto").references(PokemonTable.id, onDelete = ReferenceOption.CASCADE)
    val condition = varchar("condition", 255)
    override val primaryKey = PrimaryKey(evolveFrom, evolveTo, name = "PK_Evolution")
}