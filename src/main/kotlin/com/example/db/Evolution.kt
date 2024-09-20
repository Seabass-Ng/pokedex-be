package com.example.db

import com.example.model.Pokemon
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object EvolutionTable: Table("Evolution") {
    val evolveFrom = integer("evolveFrom").references(PokemonTable.id, onDelete = ReferenceOption.CASCADE)
    val evolveTo = integer("evolveTo").references(PokemonTable.id, onDelete = ReferenceOption.CASCADE)
    val condition = varchar("condition", 255)
    override val primaryKey = PrimaryKey(evolveFrom, evolveTo, name = "PK_Evolution")
}