package com.johnguant.redditthing.redditapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.johnguant.redditthing.redditapi.model.Link;
import com.johnguant.redditthing.redditapi.model.Thing;

import java.lang.reflect.Type;

public class ThingDeserializer implements JsonDeserializer<Thing> {
    @Override
    public Thing deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jObject = json.getAsJsonObject();
        switch (jObject.getAsJsonPrimitive("kind").getAsString()) {
            case "t3":
                Link link = context.deserialize(jObject.getAsJsonObject("data"), Link.class);
                return new Thing<>(jObject.getAsJsonPrimitive("kind").getAsString(), link);
        }
        return null;
    }
}
