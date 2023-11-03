package com.metype.hidenseek.Serializers;

import com.google.gson.*;
import com.metype.hidenseek.Game.Polygon;

import java.lang.reflect.Type;

public class PointSerializer implements JsonSerializer<Polygon.Point> {
    public JsonElement serialize(final Polygon.Point point, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.add("x", new JsonPrimitive(point.x));
        result.add("y", new JsonPrimitive(point.y));

        return result;
    }
}
