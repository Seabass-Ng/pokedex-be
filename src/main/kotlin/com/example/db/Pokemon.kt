package com.example.db

import com.example.db.MovesDAO.Companion.referrersOn
import com.example.model.Pokemon
import com.example.model.Type
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable

object PokemonTable: IdTable<Int>("pokemon") {
    override val id = integer("id").uniqueIndex().entityId()
    override val primaryKey by lazy { super.primaryKey ?: PrimaryKey(id) }
    val name = varchar("name", 50)
    val description = varchar("description", 255)
    val photo = varchar("photo", 100)
    val type1 = varchar("type1", 10)
    val type2 = varchar("type2", 10).nullable()
}

class PokemonDAO(id: EntityID<Int>): Entity<Int>(id) {
    companion object: EntityClass<Int, PokemonDAO>(PokemonTable)

    var name by PokemonTable.name
    var description by PokemonTable.description
    var photo by PokemonTable.photo
    var type1 by PokemonTable.type1
    var type2 by PokemonTable.type2
}

fun daoToModel(dao: PokemonDAO) = Pokemon(
    dao.id.value,
    dao.name,
    dao.description,
    dao.photo,
    Type.valueOf(dao.type1),
    if (dao.type2 != null && dao.type2 != "null") Type.valueOf(dao.type2!!) else null
)