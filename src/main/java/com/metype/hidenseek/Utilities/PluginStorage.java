package com.metype.hidenseek.Utilities;

import java.util.ArrayList;
import java.util.UUID;

public class PluginStorage {
    public static ArrayList<BoundsEditingPlayer> playersEditingBounds = new ArrayList<>();

    public static boolean PlayerStartEditingGameBounds(String gameKey, UUID playerID) {
        for(BoundsEditingPlayer player : playersEditingBounds) {
            if(player.id == playerID) return false;
        }
        playersEditingBounds.add(new BoundsEditingPlayer(playerID, gameKey));
        return true;
    }

    public static void PlayerStopEditingGameBounds(UUID playerID) {
        playersEditingBounds.removeIf((player) -> player.id == playerID);
    }

    public static boolean PlayerIsEditingGameBounds(UUID playerID) {
        for(BoundsEditingPlayer player : playersEditingBounds) {
            if(player.id == playerID) return true;
        }
        return false;
    }

    public static BoundsEditingPlayer GetBoundsEditingPlayer(UUID playerID) {
        for(BoundsEditingPlayer player : playersEditingBounds) {
            if(player.id == playerID) return player;
        }
        return null;
    }
}
