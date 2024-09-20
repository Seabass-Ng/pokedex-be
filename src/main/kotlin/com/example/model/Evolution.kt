package com.example.model

import kotlinx.serialization.Serializable


@Serializable
data class EvolutionModel(
    val condition: String,
    val evolvePokemon: Pokemon,
)