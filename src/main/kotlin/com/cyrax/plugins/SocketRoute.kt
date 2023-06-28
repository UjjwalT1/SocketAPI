package com.example.plugins

import com.example.getTime
import com.example.model.Message
import com.example.model.Room
import com.example.model.RoomState
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.utils.io.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import org.slf4j.LoggerFactory
import java.io.File

class LogWriter{
    val file = File("logs.txt")
    fun create(){
        val stat = file.createNewFile()
//        if(stat){
//            logger.info("\n\n\nFileCreated {}",file.absolutePath)
//        }
    }

    fun rite(str:String){
        file.appendText("$str \n")
    }

}
val NewCircles :MutableMap<String,RoomState> = mutableMapOf()
lateinit var logFile:LogWriter

val logger = LoggerFactory.getLogger("")
fun Route.socket() {
    route("/socket/{dp}"){
        webSocket {
            if(NewCircles[call.parameters["dp"]] != null){
                logFile.rite("Accepted Request on RoomID : ${call.parameters["dp"]} at ${getTime()}")
                val room = NewCircles[call.parameters["dp"]]!!
                var name:String? = null
                name = room.connect(this,call.parameters["dp"])
                try{
                    incoming.consumeEach {
                        if(it is Frame.Text){
                            try{
                                room.addMsg(Json.parseToJsonElement(it.readText()).jsonObject.toMutableMap())
                            }catch(err:Exception){
                                room.addMsg(mutableMapOf("Error" to "Malformed Json Provided"))
                            }
                        }
                        logFile.rite("Data Transmitted in RoomID ${call.parameters["dp"]} at ${getTime()}")
                    }
                }catch (e:Exception){
                    e.printStack()

                }finally {
                    room.disConnect(name,call.parameters["dp"])
                    if(room.connections.size == 0){
                        try{
                            NewCircles.remove(call.parameters["dp"])
                        }catch(_:Exception){}
                        logFile.rite("Clearing Room as Empty: ${call.parameters["dp"]} at ${getTime()}")
                    }
                }

           }else{
                logFile.rite("Rejected Request Attempt for RoomID: ${call.parameters["dp"]} at ${getTime()}")
           }
        }
    }

}