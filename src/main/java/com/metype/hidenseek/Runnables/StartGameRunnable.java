package com.metype.hidenseek.Runnables;

import com.metype.hidenseek.Main;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import com.metype.hidenseek.Utilities.StringUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StartGameRunnable implements Runnable {
    int timeUntilStart;
    String gameKey;

    public StartGameRunnable(@NonNull String gameKey, int timeUntilStart) {
        this.gameKey = gameKey;
        this.timeUntilStart = timeUntilStart;
    }

    @Override
    public void run() {
        if (this.timeUntilStart <= 0) {
            GameManager.StartGame(gameKey);
        } else {
            float timeTilStartMins = timeUntilStart / 60.0f;

            // This looks ugly, I'd love to make it nicer
            if ((((int) timeTilStartMins) % 5 == 0 && (int) (timeTilStartMins) == timeTilStartMins) || timeTilStartMins <= 1) {
                // Make a new component (Bungee API).
                TextComponent startingSoonText = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', MessageManager.GetMessageByKey("broadcast.game_starting_soon", Objects.requireNonNull(GameManager.GetGame(gameKey)).props.gameName, StringUtils.PrettyifySeconds(timeUntilStart)))));
                TextComponent startingSoonClickable = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', MessageManager.GetMessageByKey("broadcast.game_starting_soon_clickable"))));

                // Add a click event to the component.
                startingSoonClickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/hns join " + gameKey));
                startingSoonClickable.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("Click to join!")));

                startingSoonText.addExtra(startingSoonClickable);

                for(var player : JavaPlugin.getPlugin(Main.class).getServer().getOnlinePlayers()) {
                    player.spigot().sendMessage(startingSoonText);
                }
            }

            int delayTime = timeUntilStart % 30 != 0 ? timeUntilStart % 30 : 30;

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.schedule(new StartGameRunnable(gameKey, timeUntilStart - delayTime), delayTime, TimeUnit.SECONDS);
        }
    }
}
