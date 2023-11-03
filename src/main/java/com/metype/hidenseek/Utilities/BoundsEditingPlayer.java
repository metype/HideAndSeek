package com.metype.hidenseek.Utilities;

import java.util.UUID;

public class BoundsEditingPlayer {
    public UUID id;
    public String gameKey;

    public BoundsEditingPlayer(UUID playerID, String gameKey) {
        this.id = playerID;
        this.gameKey = gameKey;
    }
}
