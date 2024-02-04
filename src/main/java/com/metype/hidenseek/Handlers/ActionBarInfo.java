package com.metype.hidenseek.Handlers;

import org.bukkit.entity.Player;

import java.util.Date;

public class ActionBarInfo {
    private final Player player;

    private final Date expires;

    private final int priorityLevel;

    public ActionBarInfo(Player player, int secondsUntilExpire, int priorityLevel) {
        this.player = player;
        this.expires = new Date(new Date().getTime() + secondsUntilExpire * 1000L);
        this.priorityLevel = priorityLevel;
    }

    public boolean isExpired() {
        return !this.expires.after(new Date());
    }

    public boolean isSuperceded(int priorityLevel) {
        return priorityLevel >= this.priorityLevel;
    }

    public Player getPlayer() {
        return player;
    }
}
