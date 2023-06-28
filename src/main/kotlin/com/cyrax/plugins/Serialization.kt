package com.cyrax.plugins

import com.example.getTime
import com.example.model.RoomState
import com.example.plugins.NewCircles
import com.example.plugins.logFile
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/json/kotlinx-serialization") {
                call.respond(mapOf("hello" to "world"))
            }

        get("/getCreateID"){
            val iD = UUID.randomUUID().toString()
            NewCircles[iD] = RoomState()
            logFile.rite("Created New ID : $iD at ${getTime()}")
            call.respond(iD)
        }
    }
}
