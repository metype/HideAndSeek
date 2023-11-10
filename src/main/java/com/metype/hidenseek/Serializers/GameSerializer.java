package com.metype.hidenseek.Serializers;

import com.google.gson.*;
import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Game.Polygon;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class GameSerializer implements JsonSerializer<Game> {
    PolygonSerializer polygonSerializer;
    LocationSerializer locationSerializer;

    public GameSerializer() {
        polygonSerializer = new PolygonSerializer();
        locationSerializer = new LocationSerializer();
    }
    public JsonElement serialize(final Game game, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        Gson json = new GsonBuilder()
                .registerTypeAdapter(Polygon.class, polygonSerializer)
                .registerTypeAdapter(Location.class, locationSerializer)
                .create();

        result.add("gameStart", json.toJsonTree(game.startGameLocation));
        result.add("gameName", new JsonPrimitive(game.gameName));
        result.add("gameBounds", json.toJsonTree(game.gameBounds, Polygon.class));
        result.add("props", json.toJsonTree(game.props));

        return result;
    }
}

