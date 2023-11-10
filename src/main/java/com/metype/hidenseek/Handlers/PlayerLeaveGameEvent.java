package com.metype.hidenseek.Handlers;

import com.metype.hidenseek.Utilities.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerLeaveGameEvent extends Event {

    private PlayerLeaveGameReason reason;
    private final Player player;
    private final String gameKey;

    private static final HandlerList handlers = new HandlerList();

    public PlayerLeaveGameEvent(String gameKey, Player player, PlayerLeaveGameReason reason) {
        this.gameKey = gameKey;
        this.player = player;
        this.reason = reason;
    }

    public String getGameKey() {
        return gameKey;
    }

    public PlayerLeaveGameReason getReason() {
        return reason;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}