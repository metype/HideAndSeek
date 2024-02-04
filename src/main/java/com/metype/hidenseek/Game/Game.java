package com.metype.hidenseek.Game;

import org.bukkit.Location;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    public Polygon gameBounds = new Polygon();

    public ArrayList<UUID> players = new ArrayList<>();

    public ArrayList<UUID> seekers = new ArrayList<>();
    public ArrayList<UUID> hiders = new ArrayList<>();

    public ArrayList<OutOfBoundsPlayer> oobPlayers = new ArrayList<>();

    public boolean isActive, hasEnded = false;

    public GameProperties props = new GameProperties();

    public Location startGameLocation = null;

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Game game)) return false;
        if(!game.props.gameName.equals(props.gameName)) return false;
        if(!game.gameBounds.points.equals(gameBounds.points)) return false;
        return game.props.equals(props);
    }

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
        for(int i=0; i<gameBounds.points.size(); i++) {
            boundsStr.append(gameBounds.points.get(i).toString());
            if(i < gameBounds.points.size() - 1) {
                boundsStr.append(", ");
            } else {
                boundsStr.append("]");
            }
        }
        if(gameBounds.points.isEmpty()) boundsStr.append("]");

        return "Game{name=\"" + props.gameName + "\""
                + ", players=" + playerList
                + ", isActive=" + isActive
                + ", gameBounds=" + boundsStr + "}";
    }

//    public Object SetProperty(String propName, String propValue) throws NoSuchFieldException, IllegalAccessException, NumberFormatException {
//        Field propField = props.getClass()
//                .getDeclaredField(propName);
//        if(propField.getType() == int.class) {
//            propField.set(props, Integer.parseInt(propValue));
//            return Integer.parseInt(propValue);
//        }
//        if(propField.getType() == float.class) {
//            propField.set(props, Float.parseFloat(propValue));
//            return Float.parseFloat(propValue);
//        }
//        if(propField.getType() == boolean.class) {
//            propField.set(props, Boolean.parseBoolean(propValue));
//            return Boolean.parseBoolean(propValue);
//        }
//        return null;
//    }
//
//    public Object GetProperty(String propName) throws NoSuchFieldException, IllegalAccessException {
//        Field propField = props.getClass()
//                .getDeclaredField(propName);
//        return propField.get(props);
//    }

    public static ArrayList<String> GetPropertyNames() {
        Field[] propFields = GameProperties.class
                .getFields();
        ArrayList<String> fieldNames = new ArrayList<>();
        for(Field field : propFields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public static ArrayList<Field> GetProperties() {
        Field[] propFields = GameProperties.class
                .getFields();
        return new ArrayList<>(List.of(propFields));
    }
}
