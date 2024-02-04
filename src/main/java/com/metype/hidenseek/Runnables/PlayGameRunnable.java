package com.metype.hidenseek.Runnables;

import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Main;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import com.metype.hidenseek.Utilities.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayGameRunnable implements Runnable {
    int timeUntilGameEnd;
    String gameKey;

    public PlayGameRunnable(@NonNull String gameKey, int timeUntilGameEnd) {
        this.gameKey = gameKey;
        this.timeUntilGameEnd = timeUntilGameEnd;
    }

    @Override
    public void run() {
        if(this.timeUntilGameEnd <= 0) {
            // Handle game end logic
            GameManager.EndGame(this.gameKey);
            Game game = GameManager.GetGame(this.gameKey);
            if(game == null) return;
            if(game.props.autoNewGame) {
                GameManager.StartGame(this.gameKey, game.props.autoNewGameStartTime);
            }
        } else {
            float timeTilEndMins = timeUntilGameEnd / 60.0f;

            // This looks ugly, I'd love to make it nicer
            if((((int)timeTilEndMins) % 5 == 0 && (int)(timeTilEndMins) == timeTilEndMins) || timeTilEndMins <= 1) {
                Game game = GameManager.GetGame(gameKey);
                if(game == null) return;
                for(UUID id : game.players) {
                    Player player = JavaPlugin.getPlugin(Main.class).getServer().getPlayer(id);
                    if(player == null) continue;
                    player.sendMessage(MessageManager.GetMessageByKey("broadcast.game_ending_soon", Objects.requireNonNull(GameManager.GetGame(gameKey)).props.gameName, StringUtils.PrettyifySeconds(timeUntilGameEnd)));
                }
            }

            int delayTime = timeUntilGameEnd % 30 != 0 ? timeUntilGameEnd % 30 : 30;

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.schedule(new PlayGameRunnable(gameKey, timeUntilGameEnd - delayTime), delayTime, TimeUnit.SECONDS);
        }
    }
}