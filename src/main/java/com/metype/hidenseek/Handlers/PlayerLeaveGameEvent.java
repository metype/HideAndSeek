package com.metype.hidenseek.Handlers;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlayerLeaveGameEvent extends Event {

    private final PlayerLeaveGameReason reason;
    private final String gameKey;

    private static final HandlerList handlers = new HandlerList();

    public PlayerLeaveGameEvent(String gameKey, PlayerLeaveGameReason reason) {
        this.gameKey = gameKey;
        this.reason = reason;
    }

    public String getGameKey() {
        return gameKey;
    }

    public PlayerLeaveGameReason getReason() {
        return reason;
    }

    @Override @NonNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}