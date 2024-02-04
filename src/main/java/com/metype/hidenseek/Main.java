package com.metype.hidenseek;

import com.metype.hidenseek.Commands.HideAndSeek;
import com.metype.hidenseek.Handlers.HideAndSeekEventHandler;
import com.metype.hidenseek.Handlers.HideAndSeekTabComplete;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Objects.requireNonNull(this.getCommand("hidenseek")).setExecutor(new HideAndSeek());
        Objects.requireNonNull(this.getCommand("hidenseek")).setTabCompleter(new HideAndSeekTabComplete());

        getServer().getPluginManager().registerEvents(new HideAndSeekEventHandler(this), this);
        MessageManager.Init(this);
        GameManager.Init(this);
    }

    @Override
    public void onDisable() {
        GameManager.SaveGames();
        // Plugin shutdown logic
    }
}
