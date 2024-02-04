package com.metype.hidenseek.Handlers;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GameEndEvent extends Event {

    private final String gameKey;

    private static final HandlerList handlers = new HandlerList();

    public GameEndEvent(String gameKey) {
        super(true);
        this.gameKey = gameKey;
    }

    public String getGameKey() {
        return gameKey;
    }

    @Override @NonNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}