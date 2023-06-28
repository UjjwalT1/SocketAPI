package com.example.model


import com.example.getTime
import com.example.plugins.logFile
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory
import java.lang.ClassCastException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Serializable
data class Message(val id:String ="temp", val txt:String="temp", val sender:String="temp")

@Serializable
data class Room(val msgsList:MutableList<Message> = mutableListOf())




val logger = LoggerFactory.getLogger("aa")



data class Packet(val map : MutableMap<String,JsonElement> = mutableMapOf(),val id:String = "")
fun List<*>.toJsonElement(): JsonElement {
    val list: MutableList<JsonElement> = mutableListOf()
    this.forEach {
        val value = it as? Any ?: return@forEach
        when(value) {
            is Map<*, *> -> list.add((value).toJsonElement())
            is List<*> -> list.add(value.toJsonElement())
            else -> list.add(value as JsonElement)
        }
    }
    return JsonArray(list)
}

fun Map<*, *>.toJsonElement(): JsonElement {
    val map: MutableMap<String, JsonElement> = mutableMapOf()
    this.forEach {
        val key = it.key as? String ?: return@forEach
        val value = it.value ?: return@forEach
        when(value) {
            is Map<*, *> -> map[key] = (value).toJsonElement()
            is List<*> -> map[key] = value.toJsonElement()
            else -> {
                try{
                    map[key] = value as JsonElement
                }catch (e:Exception){
                    map[key] = JsonPrimitive(value.toString() )
                }

            }
        }
    }
    return JsonObject(map)
}






class RoomState{
    private val room = MutableStateFlow(Packet())
    val connections  = ConcurrentHashMap<String,WebSocketSession>()

    private val streamScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        room.onEach(::broadcast).launchIn(streamScope)
    }

    fun connect(session:WebSocketSession,roomID:String?): String {

        val name = UUID.randomUUID().toString()
        connections[name] = session
        logFile.rite("Connecting: ${name} whose session id is $session at RoomID: $roomID at ${getTime()}")
        return name
    }

    fun addMsg(msg:MutableMap<String,Any>){
        val id = UUID.randomUUID().toString()
        try{
            room.update {
                it.copy(
                    id = id,
                    map = mutableMapOf(id to msg.toJsonElement())
                )
            }
        }catch (err:Exception){
            room.update {
                it.copy(
                    id = id,
                    map = mutableMapOf(id to mutableMapOf("Error" to "Malformed Json Provided").toJsonElement())
                )
            }
        }

    }

    fun disConnect(name:String,roomID: String?){
        logFile.rite("Disconnecting: ${name} from RoomID: $roomID at ${getTime()}")
        connections.remove(name)

    }
    suspend fun broadcast(data : Packet){
        connections.values.forEach {webSocketSession ->
            webSocketSession.send( Json.encodeToString(data.map[data.id])
            )
        }
    }

}