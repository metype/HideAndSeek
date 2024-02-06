package com.metype.hidenseek.Events;

import com.metype.hidenseek.Handlers.PlayerLeaveGameReason;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PlayerLeaveGameEvent extends Event {

    private final PlayerLeaveGameReason reason;
    private final String gameKey;

    private final Player player;

    private static final HandlerList handlers = new HandlerList();

    public PlayerLeaveGameEvent(Player player, String gameKey, PlayerLeaveGameReason reason) {
        this.gameKey = gameKey;
        this.reason = reason;
        this.player = player;
    }

    public String getGameKey() {
        return gameKey;
    }

    public PlayerLeaveGameReason getReason() {
        return reason;
    }

    public Player getPlayer() {
        return player;
    }

    @Override @NonNull
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}