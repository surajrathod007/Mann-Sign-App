package com.surajmanshal.mannsign.network

import android.R.attr.src
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateAdapter : JsonDeserializer<LocalDate>, JsonSerializer<LocalDate> {

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun deserialize(
        json: JsonElement?,
        p1: Type?,
        p2: JsonDeserializationContext?
    ): LocalDate? {
        return LocalDate.parse(json?.asString, formatter)
    }

    override fun serialize(
        src: LocalDate?,
        p1: Type?,
        p2: JsonSerializationContext?
    ): JsonElement? {
        return if (src != null) {
            JsonPrimitive(src.format(formatter))
        } else null
    }
}