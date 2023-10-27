package com.metype.hidenseek.Serializers;

import com.google.gson.*;
import com.metype.hidenseek.Game;
import com.metype.hidenseek.Polygon;

import java.lang.reflect.Type;

public class GameSerializer implements JsonSerializer<Game> {
    public JsonElement serialize(final Game game, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        Gson json = new GsonBuilder()
                .registerTypeAdapter(Polygon.class, new PolygonSerializer())
                .create();

        result.add("minHeightBounds", new JsonPrimitive(game.minHeightBounds));
        result.add("maxHeightBounds", new JsonPrimitive(game.maxHeightBounds));
        result.add("gameName", new JsonPrimitive(game.gameName));
        result.add("gameBounds", json.toJsonTree(game.gameBounds, Polygon.class));

        return result;
    }
}

