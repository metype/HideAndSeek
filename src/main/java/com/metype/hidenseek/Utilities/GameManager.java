package com.metype.hidenseek.Utilities;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.metype.hidenseek.Deserializers.GameDeserializer;
import com.metype.hidenseek.Errors.PlayerJoinGameError;
import com.metype.hidenseek.Errors.PlayerLeaveGameError;
import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Game.OutOfBoundsPlayer;
import com.metype.hidenseek.Handlers.GameEndEvent;
import com.metype.hidenseek.Handlers.GameStartEvent;
import com.metype.hidenseek.Handlers.PlayerLeaveGameEvent;
import com.metype.hidenseek.Handlers.PlayerLeaveGameReason;
import com.metype.hidenseek.Runnables.PlayGameRunnable;
import com.metype.hidenseek.Runnables.StartGameRunnable;
import com.metype.hidenseek.Serializers.GameSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import com.google.gson.Gson;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class GameManager {
    private static HashMap<String, Game> games;

    private static final ArrayList<String> startingGames = new ArrayList<>();

    private static final ArrayList<String> activeGames = new ArrayList<>();
    private static Plugin plugin;

    private static File configDir;

    private static Gson gameSerializer;
    private static Gson gameDeserializer;

    public static void Init(Plugin p) {
        games = new HashMap<>();
        if(p != null)
            plugin = p;
        assert plugin != null;

        gameSerializer = new GsonBuilder()
                .registerTypeAdapter(Game.class, new GameSerializer())
                .setPrettyPrinting()
                .create();

        gameDeserializer = new GsonBuilder()
                .registerTypeAdapter(Game.class, new GameDeserializer())
                .create();

        ReloadGamesFromDisk();
    }

    public static void ReloadGamesFromDisk() {
        configDir = new File(plugin.getDataFolder(), "games");
        if(!configDir.exists()) {
            if(!configDir.mkdirs()) {
                plugin.getLogger().log(Level.SEVERE, "Cannot create the games directory to read/write game data to, must disable.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
        }

        FilenameFilter filter = (f, name) -> {
            // Matches any filename with the .json extension
            return name.matches(".*\\.json");
        };

        for(File gameFile : Objects.requireNonNull(configDir.listFiles(filter))) {
            try {
                StringBuilder fileContents = new StringBuilder();
                for(String line : Files.readLines(gameFile, Charset.defaultCharset())) {
                    fileContents.append(line).append("\n");
                }
                Game game = gameDeserializer.fromJson(fileContents.toString(), Game.class);
                games.put(gameFile.getName().split("\\.")[0], game);
            } catch(IOException ignored) {
                // If there's an IOException there's nothing we can really do
                plugin.getLogger().log(Level.WARNING, "Cannot read the contents of " + gameFile.getAbsolutePath() + "!");
            }
        }
    }

    public static void SaveGames() {
        if(!configDir.exists()) {
            if(!configDir.mkdirs()) {
                plugin.getLogger().log(Level.SEVERE, "Cannot create the games directory to read/write game data to, must disable.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
        }
        for(String key : games.keySet()){
            Game game = GetGame(key);
            String fileContents = gameSerializer.toJson(game);

            // Replace all non-ascii characters with underscores for sanity's sake.
            String fileName = key.replaceAll("([\0-/]|[:-@]|[\\[-`]|[{-\177])", "_");

            File jsonFile = new File(configDir, fileName+".json");
            try {
                FileWriter writer = new FileWriter(jsonFile);
                writer.write(fileContents);
                writer.close();
            } catch (IOException ignored) {
                plugin.getLogger().log(Level.WARNING, "Cannot write to the contents of " + jsonFile.getAbsolutePath() + "!");
            }
        }
    }

    public static void NewGame(@NonNull String gameKey, @NonNull Game newGame) {
        games.put(gameKey, newGame);
    }
    public static void DeleteGame(@NonNull String gameKey) {
        games.remove(gameKey);
    }

    public static List<String> GetGames() {
        return new ArrayList<>(games.keySet());
    }

    public static Game GetGame(@NonNull String key) {
        if(!games.containsKey(key)) return null;
        return games.get(key);
    }

    public static Game GetGame(@NonNull UUID playerID) {
        if(!IsPlayerInAnyGame(playerID)) return null;
        for(String gameKey : activeGames) {
            Game game = GetGame(gameKey);
            assert game != null;
            if(game.players.contains(playerID)) {
                return game;
            }
        }
        for(String gameKey : startingGames) {
            Game game = GetGame(gameKey);
            assert game != null;
            if(game.players.contains(playerID)) {
                return game;
            }
        }
        return null;
    }

    public static boolean IsPlayerInGame(@NonNull String gameKey, @NonNull UUID playerID) {
        Game game = GetGame(gameKey);
        if(game == null) return false;

        return game.players.contains(playerID);
    }

    public static boolean IsPlayerInAnyGame(@NonNull UUID playerID) {
        boolean playerInGame = false;

        for(Game game : games.values()) {
            if (game.players.contains(playerID)) {
                playerInGame = true;
                break;
            }
        }

//        if(playerInGame)
//            System.out.println("Player " + playerID + " is in a game!");

        return playerInGame;
    }

    public static boolean IsPlayerOutOfBounds(@NonNull UUID playerID) {
        if(!IsPlayerInAnyGame(playerID)) return false;
        Game game = GetGame(playerID);
        if(game == null) return false;
        for(OutOfBoundsPlayer oobPlayer : game.oobPlayers) {
            if(oobPlayer.id == playerID) return true;
        }
        return false;
    }

    public static PlayerJoinGameError PutPlayerInGame(@NonNull String gameKey, @NonNull UUID playerID) {
        Game game = GetGame(gameKey);
        if(game == null) return PlayerJoinGameError.GameDoesNotExist;
        if(IsPlayerInAnyGame(playerID)) return PlayerJoinGameError.PlayerAlreadyInGame;
        if(!startingGames.contains(gameKey) && !activeGames.contains(gameKey)) return PlayerJoinGameError.GameInactive;
        if(!game.hasEnded && activeGames.contains(gameKey)) return PlayerJoinGameError.GameInProgress;

        game.players.add(playerID);
        return PlayerJoinGameError.Okay;
    }

    public static PlayerLeaveGameError RemovePlayerFromGame(@NonNull String gameKey, @NonNull UUID playerID, PlayerLeaveGameReason reason) {
        Game game = GetGame(gameKey);
        if(game == null) return PlayerLeaveGameError.GameDoesNotExist;
        if(!IsPlayerInGame(gameKey, playerID)) return PlayerLeaveGameError.PlayerNotInGame;

        game.oobPlayers.removeIf((player) -> player.id == playerID);
        game.players.remove(playerID);
        game.hiders.remove(playerID);
        game.seekers.remove(playerID);

        CallEventSync(new PlayerLeaveGameEvent(gameKey, reason));

        return PlayerLeaveGameError.Okay;
    }

    public static PlayerLeaveGameError RemovePlayerFromAllGames(@NonNull UUID playerID, PlayerLeaveGameReason reason) {
        if(!IsPlayerInAnyGame(playerID)) return PlayerLeaveGameError.PlayerNotInGame;

        for(String gameKey : GetGames()) {
            var err = RemovePlayerFromGame(gameKey, playerID, reason);
            if(err != PlayerLeaveGameError.Okay) {
                plugin.getLogger().log(Level.WARNING, "Could not remove player from game for reason: " + err);
            }
        }

        return PlayerLeaveGameError.Okay;
    }

    public static PlayerLeaveGameError DisqualifyPlayer(@NonNull UUID playerID) {
        Player player = plugin.getServer().getPlayer(playerID);
        if(player == null) return PlayerLeaveGameError.PlayerNotInGame;
        return DisqualifyPlayer(player);
    }

    public static PlayerLeaveGameError DisqualifyPlayer(@NonNull Player player) {
        if(!IsPlayerInAnyGame(player.getUniqueId())) return PlayerLeaveGameError.PlayerNotInGame;

        HandleDisqualified(player);

        return PlayerLeaveGameError.Okay;
    }

    public static boolean IsGameStarting(@NonNull String gameKey) {
        return startingGames.contains(gameKey);
    }

    public static void StartGame(@NonNull String gameKey, int timeUntilStart) {
        if(GetGame(gameKey) == null) return;
        startingGames.add(gameKey);
        HandleStartingGame(gameKey, timeUntilStart);
    }

    public static void CancelGame(@NonNull String gameKey) {
        if(startingGames.contains(gameKey) || activeGames.contains(gameKey)) {
            Game game = InternalEndGame(gameKey);
            plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_cancelled", game.props.gameName));
        }
    }

    public static void EndGame(@NonNull String gameKey) {
        if(startingGames.contains(gameKey)) {
            CancelGame(gameKey);
            return;
        }
        if(activeGames.contains(gameKey)) {
            Game game = InternalEndGame(gameKey);
            plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_ending", game.props.gameName));
        }
    }

    public static void ResetGame(@NonNull String gameKey) {
        Game game = GetGame(gameKey);
        if(game == null) return;

        for(UUID player : game.players) {
            game.hiders.remove(player);
            game.seekers.remove(player);
            game.oobPlayers.removeIf((oobPlayer) -> oobPlayer.id == player);
        }
    }

    public static void RestartGame(@NonNull String gameKey) {
        StartGame(gameKey);
    }

    private static Game InternalEndGame(@NonNull String gameKey) {
        Game game = GetGame(gameKey);
        assert game != null;
        var idList = game.players.stream().toList();
        for(var playerId : idList) {
            HandlePlayerLeaveGame(gameKey, plugin.getServer().getPlayer(playerId), PlayerLeaveGameReason.GAME_END);
        }

        startingGames.remove(gameKey);
        activeGames.remove(gameKey);

        CallEventAsync(new GameEndEvent(gameKey));

        return game;
    }

    private static Event CallEventAsync(Event event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }
        }.runTaskAsynchronously(plugin);
        return event;
    }

    private static Event CallEventSync(Event event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(event);
            }
        }.runTask(plugin);
        return event;
    }

    public static String GetKey(@NonNull Game game) {
        for(String s : games.keySet()) {
            if(games.get(s) == game) {
                return s;
            }
        }
        return null;
    }

    public static ArrayList<String> GetJoinableGames() {
        ArrayList<String> joinableGames = new ArrayList<>(startingGames);
        joinableGames.addAll(activeGames);
        return joinableGames;
    }

    public static ArrayList<String> GetActiveGames() {
        return activeGames;
    }

    public static void StartGame(@NonNull String gameKey) {
        startingGames.remove(gameKey);
        if(!activeGames.contains(gameKey))
            activeGames.add(gameKey);

        GameStartEvent gameStart = (GameStartEvent) CallEventAsync(new GameStartEvent(gameKey));
    }

    private static void HandlePlayerLeaveGame(@NonNull String gameKey, @Nullable Player player, PlayerLeaveGameReason reason) {
        if(player == null) return;
        player.sendMessage(MessageManager.GetMessageByKey("info.leave_game", Objects.requireNonNull(GetGame(gameKey)).props.gameName));
        player.removePotionEffect(PotionEffectType.SPEED);
        RemovePlayerFromAllGames(player.getUniqueId(), reason);
    }

    private static void HandleDisqualified(Player player) {
        player.sendMessage(MessageManager.GetMessageByKey("info.disqualified"));
        RemovePlayerFromAllGames(player.getUniqueId(), PlayerLeaveGameReason.DISQUALIFICATION);
    }

    private static void HandleStartingGame(@NonNull String gameKey, int timeUntilStart) {
        new StartGameRunnable(gameKey, timeUntilStart).run();
    }
}
