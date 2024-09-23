package com.example.plugins

import com.example.model.Pokemon
import com.example.repository.IPokemonRepository
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSerialization(repository: IPokemonRepository) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        route("/pokemon") {
            get {
                val pokemons = repository.pokemons()
                call.respond(pokemons)
            }

            get("/{id}") {
                val pokemonId = call.parameters["id"]?.toInt()
                if (pokemonId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val pokemon = repository.pokemon(pokemonId)
                if (pokemon == null) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }
                call.respond(pokemon)
            }

            get("/name/{name}") {
                val pokemonName = call.parameters["name"]
                if (pokemonName == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val pokemons = repository.pokemonByName(pokemonName)
                call.respond(pokemons)
            }

            get("/{id}/moves") {
                val pokemonId = call.parameters["id"]?.toInt()
                if (pokemonId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val moves = repository.getAllMovesFromPokemon(pokemonId)
                call.respond(moves)
            }

            get("/{id}/evolveFrom") {
                val pokemonId = call.parameters["id"]?.toInt()
                if (pokemonId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val evolveFrom = repository.getEvolvedFrom(pokemonId)
                if (evolveFrom == null) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(evolveFrom)
                }
            }

            get("/{id}/evolveTo") {
                val pokemonId = call.parameters["id"]?.toInt()
                if (pokemonId == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@get
                }
                val evolveTo = repository.getEvolvedTo(pokemonId)
                call.respond(evolveTo)
            }

            post {
                try {
                    val pokemon = call.receive<Pokemon>()
                    repository.addPokemon(pokemon)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }

            delete("/{id}") {
                val id = call.parameters["id"]?.toInt()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@delete
                }
                if (repository.removePokemon(id)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
        get("/moves") {
            val moves = repository.moves()
            call.respond(moves)
        }
    }
}
