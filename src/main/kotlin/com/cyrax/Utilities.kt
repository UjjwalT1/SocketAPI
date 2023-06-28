package com.example

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun getTime():String {
    val timestamp = System.currentTimeMillis()/1000

    val dateTime = LocalDateTime.ofInstant(
        Instant.ofEpochSecond(timestamp),
        ZoneId.of("Asia/Kolkata")
    )

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val formattedTime = dateTime.format(formatter)

    return ("Time : $formattedTime")
}