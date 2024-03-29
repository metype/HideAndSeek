package com.metype.hidenseek.Events;
import com.metype.hidenseek.Utilities.GameManager;
import org.bukkit.event.Cancellable;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GameStartEvent extends Event implements Cancellable {

    private final String gameKey;
    private boolean cancelled;

    private static final HandlerList handlers = new HandlerList();

    public GameStartEvent(String gameKey) {
        super(true);
        this.gameKey = gameKey;
    }

    public String getGameKey() {
        return gameKey;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean arg0) {
        cancelled = arg0;
        if (cancelled) GameManager.CancelGame(gameKey);
    }

    @Override @NonNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}