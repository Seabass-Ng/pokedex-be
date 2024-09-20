package com.example.db

import com.example.model.Moves
import com.example.model.Pokemon
import com.example.model.Type
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ResultRow

object MovesTable: IntIdTable("pokemon_moves") {
    val name = varchar("name", 50)
    val pp = integer("pp")
    val accuracy = integer("accuracy").nullable()
    val type = varchar("type", 10)
    val power = integer("power").nullable()
}

class MovesDAO(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<MovesDAO>(MovesTable)
    var name by MovesTable.name
    var pp by MovesTable.pp
    var accuracy by MovesTable.accuracy
    var type by MovesTable.type
    var power by MovesTable.power
}

fun daoToModel(dao: MovesDAO) = Moves(
    dao.name,
    dao.pp,
    dao.accuracy,
    Type.valueOf(dao.type),
    dao.power,
)
