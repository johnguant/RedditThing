package com.johnguant.redditthing.redditapi

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import com.johnguant.redditthing.redditapi.model.Link
import com.johnguant.redditthing.redditapi.model.Thing

import java.lang.reflect.Type

class ThingDeserializer : JsonDeserializer<Thing<*>> {
    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Thing<*>? {
        val jObject = json.asJsonObject
        when (jObject.getAsJsonPrimitive("kind").asString) {
            "t3" -> {
                val link = context.deserialize<Link>(jObject.getAsJsonObject("data"), Link::class.java)
                return Thing(jObject.getAsJsonPrimitive("kind").asString, link)
            }
        }
        return null
    }
}
