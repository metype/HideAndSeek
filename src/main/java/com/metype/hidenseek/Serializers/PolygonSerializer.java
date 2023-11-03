package com.metype.hidenseek.Serializers;

import com.google.gson.*;
import com.metype.hidenseek.Game.Polygon;

import java.lang.reflect.Type;

public class PolygonSerializer implements JsonSerializer<Polygon> {
    PointSerializer pointSerializer;

    public PolygonSerializer() {
        pointSerializer = new PointSerializer();
    }

    public JsonElement serialize(final Polygon poly, final Type type, final JsonSerializationContext context) {
        JsonArray pointsJsonArray = new JsonArray();

        Gson json = new GsonBuilder()
                .registerTypeAdapter(Polygon.Point.class, pointSerializer)
                .create();

        for(int i = 0; i < poly.points.size(); i++){
            pointsJsonArray.add(json.toJsonTree(poly.points.get(i), Polygon.Point.class));
        }

        return pointsJsonArray;
    }
}

