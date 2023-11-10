package com.metype.hidenseek.Serializers;

import com.google.gson.*;
import com.metype.hidenseek.Game.Polygon;
import org.bukkit.Location;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;

public class LocationSerializer implements JsonSerializer<Location> {
    public JsonElement serialize(final Location loc, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        result.add("world", new JsonPrimitive(Objects.requireNonNull(loc.getWorld()).getName()));
        result.add("x", new JsonPrimitive(loc.getX()));
        result.add("y", new JsonPrimitive(loc.getY()));
        result.add("z", new JsonPrimitive(loc.getZ()));
        result.add("yaw", new JsonPrimitive(loc.getYaw()));
        result.add("pitch", new JsonPrimitive(loc.getPitch()));


        return result;
    }
}
