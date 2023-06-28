package com.cyrax.plugins

import com.example.plugins.socket
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Welcome to the Home Page of Socket API. Its only a server. \nThis API uses Ktor framework. To learn more about how to use this API visit here : \n")
        }
        socket()
    }
}
