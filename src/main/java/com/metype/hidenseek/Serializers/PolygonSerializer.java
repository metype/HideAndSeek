package com.metype.hidenseek.Serializers;

import com.google.gson.*;
import com.metype.hidenseek.Polygon;

import java.lang.reflect.Type;

public class PolygonSerializer implements JsonSerializer<Polygon> {
    public JsonElement serialize(final Polygon poly, final Type type, final JsonSerializationContext context) {
        JsonArray pointsJsonArray = new JsonArray(poly.points.length);

        Gson json = new GsonBuilder()
                .registerTypeAdapter(Polygon.Point.class, new PointSerializer())
                .create();

        for(int i = 0; i < pointsJsonArray.size(); i++){
            pointsJsonArray.set(i, json.toJsonTree(poly.points[i], Polygon.Point.class));
        }

        return pointsJsonArray;
    }
}

