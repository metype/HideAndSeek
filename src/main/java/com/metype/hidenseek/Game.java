package com.metype.hidenseek;

import java.util.ArrayList;
import java.util.UUID;

public class Game {
    public Polygon gameBounds = new Polygon();

    public String gameName = "";

    public ArrayList<UUID> players = new ArrayList<>();

    public boolean isActive, invisibleSeekers;

    public int minHeightBounds, maxHeightBounds, timeUntilStart, gameLength = 300,
            seekerSpeedStrength = 2, outOfBoundsTime = 10;

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
        if(players.size() == 0) playerList.append("]");

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
}
