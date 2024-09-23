package com.example.repository

import com.example.db.*
import com.example.model.*
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

    override suspend fun getEvolvedTo(id: Int): List<EvolutionModel> = suspendTransaction {
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
                pokemon2[PokemonTable.type2],
                EvolutionTable.condition
            )
            .where { pokemon1[PokemonTable.id] eq id }
            .orderBy(pokemon2[PokemonTable.id])
            .map {
                EvolutionModel(
                    it[EvolutionTable.condition],
                    Pokemon(
                        it[pokemon2[PokemonTable.id]].value,
                        it[pokemon2[PokemonTable.name]],
                        it[pokemon2[PokemonTable.description]],
                        it[pokemon2[PokemonTable.photo]],
                        Type.valueOf(it[pokemon2[PokemonTable.type1]]),
                        if (it[pokemon2[PokemonTable.type2]] != null) Type.valueOf(it[pokemon2[PokemonTable.type2]]!!) else null
                    )
                )
            }
    }

    override suspend fun getEvolvedFrom(id: Int): EvolutionModel? = suspendTransaction {
        val pokemon1 = Alias(PokemonTable, "pokemon1")
        val pokemon2 = Alias(PokemonTable, "pokemon2")

        val retVal = pokemon1
            .innerJoin(EvolutionTable, { pokemon1[PokemonTable.id] }, { EvolutionTable.evolveFrom })
            .innerJoin(pokemon2, { pokemon2[PokemonTable.id] }, { EvolutionTable.evolveTo })
            .select(
                pokemon1[PokemonTable.id],
                pokemon1[PokemonTable.name],
                pokemon1[PokemonTable.description],
                pokemon1[PokemonTable.photo],
                pokemon1[PokemonTable.type1],
                pokemon1[PokemonTable.type2],
                EvolutionTable.condition
            )
            .where { pokemon2[PokemonTable.id] eq id }
            .limit(1)
            .map {
                EvolutionModel(
                    it[EvolutionTable.condition],
                    Pokemon(
                        it[pokemon1[PokemonTable.id]].value,
                        it[pokemon1[PokemonTable.name]],
                        it[pokemon1[PokemonTable.description]],
                        it[pokemon1[PokemonTable.photo]],
                        Type.valueOf(it[pokemon1[PokemonTable.type1]]),
                        if (it[pokemon1[PokemonTable.type2]] != null) Type.valueOf(it[pokemon1[PokemonTable.type2]]!!) else null
                    )
                )
            }
        if(retVal.size > 0) retVal[0] else null
    }
}