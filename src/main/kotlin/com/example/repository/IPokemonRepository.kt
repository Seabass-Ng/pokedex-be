package com.example.repository

import com.example.model.EvolutionModel
import com.example.model.Moves
import com.example.model.Pokemon
import com.example.model.PokemonMoves

interface IPokemonRepository {
    suspend fun pokemons(): List<Pokemon>
    suspend fun pokemon(id: Int): Pokemon?
    suspend fun pokemonByName(name: String): List<Pokemon>
    suspend fun addPokemon(pokemon: Pokemon)
    suspend fun removePokemon(id: Int): Boolean
    suspend fun moves(): List<Moves>
    suspend fun getAllMovesFromPokemon(id: Int): List<PokemonMoves>
    suspend fun getEvolvedFrom(id: Int): EvolutionModel?
    suspend fun getEvolvedTo(id: Int): List<EvolutionModel>
}