package com.metype.hidenseek.Deserializers;

import com.google.gson.*;
import com.metype.hidenseek.Game;
import com.metype.hidenseek.Polygon;

import java.lang.reflect.Type;

public class GameDeserializer implements JsonDeserializer<Game> {
    @Override
    public Game deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Game game = new Game();
        JsonObject obj = jsonElement.getAsJsonObject();
        game.minHeightBounds = obj.get("minHeightBounds").getAsInt();
        game.maxHeightBounds = obj.get("maxHeightBounds").getAsInt();
        game.gameName = obj.get("gameName").getAsString();

        JsonArray points = obj.get("gameBounds").getAsJsonArray();

        game.gameBounds = new Polygon(points.size());
//        for(int i = 0;i < points.size(); i++) {
//            game.gameBounds.points[i] = new Polygon.Point();
//        }
        return game;
    }
}
