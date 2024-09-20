package com.example.repository

import com.example.db.*
import com.example.model.Moves
import com.example.model.Pokemon
import com.example.model.PokemonMoves
import com.example.model.Type
import org.jetbrains.exposed.sql.Alias
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin

class PokemonRepository : IPokemonRepository {
    override suspend fun pokemons(): List<Pokemon> = suspendTransaction {
        // TODO: Figure out a way to have the dao do the sorting.
        PokemonDAO.all().map(::daoToModel).sortedBy { it.id }
    }

    override suspend fun pokemon(id: Int): Pokemon? = suspendTransaction {
        PokemonDAO
            .find { (PokemonTable.id eq id) }
            .limit(1)
            .map(::daoToModel)
            .firstOrNull()
    }

    override suspend fun pokemonByName(name: String): List<Pokemon> = suspendTransaction{
        PokemonDAO
            .find { (PokemonTable.name like "%$name%") }
            .map(::daoToModel)
    }

    override suspend fun addPokemon(pokemon: Pokemon): Unit = suspendTransaction {
        try {
            PokemonDAO.new(pokemon.id) {
                name = pokemon.name
                description = pokemon.description
                photo = pokemon.photo
                type1 = pokemon.type1.toString()
                type2 = if (pokemon.type2 != null) pokemon.type2.toString() else null
            }
        } catch (e: NoClassDefFoundError) {
            System.err.println("Something's up");
        }
    }

    override suspend fun removePokemon(id: Int): Boolean = suspendTransaction {
        val rowsDeleted = PokemonTable.deleteWhere {
            PokemonTable.id eq id
        }
        rowsDeleted == 1
    }

    override suspend fun moves(): List<Moves> = suspendTransaction {
        MovesDAO.all().map(::daoToModel)
    }

    override suspend fun getAllMovesFromPokemon(id: Int): List<PokemonMoves> = suspendTransaction {
        PokemonTable
            .innerJoin(PokemonMovesTable, { PokemonTable.id }, { PokemonMovesTable.pokemon })
            .innerJoin(MovesTable, { PokemonMovesTable.move }, { MovesTable.id })
            .select(MovesTable.name, MovesTable.pp, MovesTable.accuracy, MovesTable.type, MovesTable.power, PokemonMovesTable.level)
            .where { PokemonTable.id eq id }
            .orderBy(PokemonMovesTable.level)
            .map {
                PokemonMoves(
                    it[MovesTable.name],
                    it[MovesTable.pp],
                    it[MovesTable.accuracy],
                    Type.valueOf(it[MovesTable.type]),
                    it[MovesTable.power],
                    it[PokemonMovesTable.level]
                )
            }
    }

    override suspend fun getEvolvedTo(id: Int): List<Pokemon> = suspendTransaction {
        val pokemon1 = Alias(PokemonTable, "pokemon1")
        val pokemon2 = Alias(PokemonTable, "pokemon2")

        pokemon1
            .innerJoin(EvolutionTable, { pokemon1[PokemonTable.id] }, { EvolutionTable.evolveFrom })
            .innerJoin(pokemon2, { pokemon2[PokemonTable.id] }, { EvolutionTable.evolveTo })
            .select(
                pokemon2[PokemonTable.id],
                pokemon2[PokemonTable.name],
                pokemon2[PokemonTable.description],
                pokemon2[PokemonTable.photo],
                pokemon2[PokemonTable.type1],
                pokemon2[PokemonTable.type2]
            )
            .where { pokemon1[PokemonTable.id] eq id }
            .orderBy(pokemon2[PokemonTable.id])
            .map {
                Pokemon(
                    it[PokemonTable.id].value,
                    it[PokemonTable.name],
                    it[PokemonTable.description],
                    it[PokemonTable.photo],
                    Type.valueOf(it[PokemonTable.type1]),
                    if (it[PokemonTable.type2] != null) Type.valueOf(it[PokemonTable.type2]!!) else null
                )
            }
    }
}