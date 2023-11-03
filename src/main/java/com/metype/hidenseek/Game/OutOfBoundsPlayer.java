package com.metype.hidenseek.Game;

import java.util.UUID;

public class OutOfBoundsPlayer {

    public OutOfBoundsPlayer(UUID id, long timeLeftBounds) {
        this.id = id;
        this.timeLeftBounds = timeLeftBounds;
    }

    public UUID id;
    public long timeLeftBounds;
}
