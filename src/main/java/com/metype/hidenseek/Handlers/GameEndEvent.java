package com.metype.hidenseek.Handlers;

import com.metype.hidenseek.Utilities.GameManager;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}