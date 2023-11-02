package com.metype.hidenseek;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public class Game {
    public Polygon gameBounds = new Polygon();

    public String gameName = "";

    public ArrayList<UUID> players = new ArrayList<>();

    public boolean isActive, invisibleSeekers;

    public int minHeightBounds, maxHeightBounds, timeUntilStart;

    public GameProperties props;

    public String toString() {
        StringBuilder playerList = new StringBuilder("[");
        for(int i=0; i<players.size(); i++) {
            playerList.append(players.get(i).toString());
            if(i < players.size() - 1) {
                playerList.append(", ");
            } else {
                playerList.append("]");
            }
        }

        if(players.isEmpty()) playerList.append("]");

        StringBuilder boundsStr = new StringBuilder("[");
        for(int i=0; i<gameBounds.points.length; i++) {
            boundsStr.append(gameBounds.points[i].toString());
            if(i < gameBounds.points.length - 1) {
                boundsStr.append(", ");
            } else {
                boundsStr.append("]");
            }
        }
        if(gameBounds.points.length == 0) boundsStr.append("]");

        return "Game{name=\"" + gameName + "\""
                + ", players=" + playerList
                + ", isActive=" + isActive
                + ", minHeight=" + minHeightBounds
                + ", maxHeight=" + maxHeightBounds
                + ", gameBounds=" + boundsStr + "}";
    }

    public Object SetProperty(String propName, String propValue) throws NoSuchFieldException, IllegalAccessException {
        Field propField = props.getClass()
                .getDeclaredField(propName);
        if(propField.getType() == Integer.class) {
            propField.set(props, Integer.parseInt(propValue));
            return Integer.parseInt(propValue);
        }
        if(propField.getType() == Float.class) {
            propField.set(props, Float.parseFloat(propValue));
            return Float.parseFloat(propValue);
        }
        if(propField.getType() == Boolean.class) {
            propField.set(props, Boolean.parseBoolean(propValue));
            return Boolean.parseBoolean(propValue);
        }
        return null;
    }
}
