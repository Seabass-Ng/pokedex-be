package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class PokemonMoves (
    val name: String,
    val pp: Int,
    val accuracy: Int?,
    val type: Type,
    val power: Int?,
    val level: Int,
)