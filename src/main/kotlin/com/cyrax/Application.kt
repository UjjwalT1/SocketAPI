package com.cyrax

import io.ktor.server.application.*
import com.cyrax.plugins.*
import com.example.plugins.LogWriter
import com.example.plugins.logFile

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    val logFiles  = LogWriter()
    logFiles.create()
    logFile = logFiles
    configureSockets()
    configureSerialization()
    configureMonitoring()
    configureHTTP()
    configureRouting()
}
