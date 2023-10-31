package com.metype.hidenseek.Deserializers;

import com.google.gson.*;
import com.metype.hidenseek.Game;
import com.metype.hidenseek.Polygon;

import java.lang.reflect.Type;

public class GameDeserializer implements JsonDeserializer<Game> {
    @Override
    public Game deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Game gameObj = new Game();

        JsonObject gameObject = jsonElement.getAsJsonObject();

        JsonElement minHeightBounds = gameObject.get("minHeightBounds");
        if(minHeightBounds == null) gameObj.minHeightBounds = 0;
        else gameObj.minHeightBounds = minHeightBounds.getAsInt();

        JsonElement maxHeightBounds = gameObject.get("maxHeightBounds");
        if(maxHeightBounds == null) gameObj.maxHeightBounds = 0;
        else gameObj.maxHeightBounds = maxHeightBounds.getAsInt();

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

                gameBounds.points[i] = point;
            }
        }

        gameObj.gameBounds = gameBounds;

        return gameObj;
    }
}
