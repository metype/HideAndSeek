package com.metype.hidenseek.Deserializers;

import com.google.gson.*;
import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Game.GameProperties;
import com.metype.hidenseek.Game.Polygon;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class GameDeserializer implements JsonDeserializer<Game> {
    @Override
    public Game deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Game gameObj = new Game();

        JsonObject gameObject = jsonElement.getAsJsonObject();

        JsonElement gameName = gameObject.get("gameName");
        if(gameName == null) throw new JsonParseException("No game name!");
        else gameObj.gameName = gameName.getAsString();

        Polygon gameBounds;

        JsonElement gameBoundsArr = gameObject.get("gameBounds");
        if(gameBoundsArr == null) throw new JsonParseException("No game bounds!");
        else {
            JsonArray pointsArr = gameBoundsArr.getAsJsonArray();
            gameBounds = new Polygon(pointsArr.size());
            for (int i=0; i<pointsArr.size(); i++) {
                Polygon.Point point = new Polygon.Point(0,0);

                JsonElement xPos = pointsArr.get(i).getAsJsonObject().get("x");
                if(xPos != null) point.x = xPos.getAsInt();

                JsonElement yPos = pointsArr.get(i).getAsJsonObject().get("y");
                if(yPos != null) point.y = yPos.getAsInt();

                gameBounds.points.set(i, point);
            }
        }

        gameObj.gameBounds = gameBounds;

        JsonElement gameProps = gameObject.get("props");
        if(gameProps == null) gameObj.props = new GameProperties();
        else gameObj.props = (new Gson()).fromJson(gameProps, GameProperties.class);

        JsonElement gameStartObj = gameObject.get("gameStart");
        if(gameStartObj == null) gameObj.startGameLocation = null;
        else {
            JsonObject startObj = gameStartObj.getAsJsonObject();
            String worldName = startObj.get("world").getAsString();
            float x = startObj.get("x").getAsFloat();
            float y = startObj.get("y").getAsFloat();
            float z = startObj.get("z").getAsFloat();
            float yaw = startObj.get("yaw").getAsFloat();
            float pitch = startObj.get("pitch").getAsFloat();
            gameObj.startGameLocation = new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
        }

        return gameObj;
    }
}
