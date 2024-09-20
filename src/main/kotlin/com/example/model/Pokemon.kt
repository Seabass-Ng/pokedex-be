package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class Pokemon (
    val id: Int,
    val name: String,
    val description: String,
    val photo: String,
    val type1: Type,
    val type2: Type? = null
)