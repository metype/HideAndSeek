package com.metype.hidenseek.Utilities;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.metype.hidenseek.Deserializers.GameDeserializer;
import com.metype.hidenseek.Errors.PlayerJoinGameError;
import com.metype.hidenseek.Errors.PlayerLeaveGameError;
import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Game.OutOfBoundsPlayer;
import com.metype.hidenseek.Game.OutOfBoundsTimer;
import com.metype.hidenseek.Handlers.GameStartEvent;
import com.metype.hidenseek.Serializers.GameSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
import org.checkerframework.checker.nullness.qual.NonNull;

public class GameManager {
    private static HashMap<String, Game> games;

    private static ArrayList<String> startingGames = new ArrayList<>();

    private static ArrayList<String> activeGames = new ArrayList<>();
    private static Plugin plugin;

    private static File configDir;

    private static Gson gameSerializer;
    private static Gson gameDeserializer;

    static class StartGameRunnable implements Runnable {
        int timeUntilStart = 0;
        String gameKey = "";

        public StartGameRunnable(@NonNull String gameKey, int timeUntilStart) {
            this.gameKey = gameKey;
            this.timeUntilStart = timeUntilStart;
        }

        @Override
        public void run() {
            if(this.timeUntilStart <= 0) {
                StartGame(gameKey);
            } else {
                float timeTilStartMins = timeUntilStart / 60.0f;

                // This looks ugly, I'd love to make it nicer
                if((((int)timeTilStartMins) % 5 == 0 && (int)(timeTilStartMins)==timeTilStartMins) || timeTilStartMins <= 1) {
                    plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_starting_soon", Objects.requireNonNull(GetGame(gameKey)).gameName, PrettyifySeconds(timeUntilStart)));
                }

                int delayTime = timeUntilStart % 30 != 0 ? timeUntilStart % 30 : 30;

                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.schedule(new StartGameRunnable(gameKey, timeUntilStart - delayTime), delayTime, TimeUnit.SECONDS);
            }
        }
    }

    static class PlayGameRunnable implements Runnable {
        int timeUntilGameEnd = 0;
        String gameKey = "";

        public PlayGameRunnable(@NonNull String gameKey, int timeUntilGameEnd) {
            this.gameKey = gameKey;
            this.timeUntilGameEnd = timeUntilGameEnd;
        }

        @Override
        public void run() {
            if(this.timeUntilGameEnd <= 0) {
                // Handle game end logic
            } else {
                float timeTilEndMins = timeUntilGameEnd / 60.0f;

                // This looks ugly, I'd love to make it nicer
                if((((int)timeTilEndMins) % 5 == 0 && (int)(timeTilEndMins) == timeTilEndMins) || timeTilEndMins <= 1) {
                    Game game = GetGame(gameKey);
                    if(game == null) return;
                    for(UUID id : game.players) {
                        Player player = plugin.getServer().getPlayer(id);
                        if(player == null) continue;
                        player.sendMessage(MessageManager.GetMessageByKey("broadcast.game_ending_soon", Objects.requireNonNull(GetGame(gameKey)).gameName, PrettyifySeconds(timeUntilGameEnd)));
                    }
                }

                int delayTime = timeUntilGameEnd % 30 != 0 ? timeUntilGameEnd % 30 : 30;

                ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                executor.schedule(new PlayGameRunnable(gameKey, timeUntilGameEnd - delayTime), delayTime, TimeUnit.SECONDS);
            }
        }
    }

    protected static String PrettyifySeconds(int secondsNum) {
        String val = "";
        if(secondsNum >= 60) {
            int minuteVal = ((int)Math.floor(secondsNum / 60.0));
            val += minuteVal + " minute" + ((minuteVal > 1)?"s":"");
            if(secondsNum % 60 > 0) {
                val += " and " + (secondsNum%60) + " seconds";
            }
        } else {
            val += (secondsNum%60) + " seconds";
        }
        return val;
    }

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

        game.players.add(playerID);
        return PlayerJoinGameError.Okay;
    }

    public static PlayerLeaveGameError RemovePlayerFromGame(@NonNull String gameKey, @NonNull UUID playerID) {
        Game game = GetGame(gameKey);
        if(game == null) return PlayerLeaveGameError.GameDoesNotExist;
        if(!IsPlayerInAnyGame(playerID)) return PlayerLeaveGameError.PlayerNotInGame;

        game.oobPlayers.removeIf((player) -> player.id == playerID);
        game.players.remove(playerID);
        game.hiders.remove(playerID);
        game.seekers.remove(playerID);
        return PlayerLeaveGameError.Okay;
    }

    public static PlayerLeaveGameError RemovePlayerFromAllGames(@NonNull UUID playerID) {
        if(!IsPlayerInAnyGame(playerID)) return PlayerLeaveGameError.PlayerNotInGame;

        for(Game game : games.values()) {
            game.oobPlayers.removeIf((player) -> player.id == playerID);
            game.players.remove(playerID);
            game.hiders.remove(playerID);
            game.seekers.remove(playerID);
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

    public static void StartGame(@NonNull String gameKey, int timeUntilStart) {
        if(GetGame(gameKey) == null) return;
        startingGames.add(gameKey);
        HandleStartingGame(gameKey, timeUntilStart);
    }

    public static void CancelGame(@NonNull String gameKey) {
        if(startingGames.contains(gameKey) || activeGames.contains(gameKey)) {
            startingGames.remove(gameKey);
            activeGames.remove(gameKey);

            Game game = GetGame(gameKey);
            if(game == null) return;
            for(UUID id : game.players) {
                Player player = plugin.getServer().getPlayer(id);
                if(player == null) continue;
                HandlePlayerLeaveGame(gameKey, player);
            }
            plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_cancelled", game.gameName));
        }
    }

    public static ArrayList<String> GetJoinableGames() {
        ArrayList<String> joinableGames = (ArrayList<String>) startingGames.clone();
        joinableGames.addAll(activeGames);
        return joinableGames;
    }

    public static ArrayList<String> GetActiveGames() {
        return activeGames;
    }

    private static void StartGame(@NonNull String gameKey) {
        startingGames.remove(gameKey);
        activeGames.add(gameKey);
        GameStartEvent gameStart = new GameStartEvent(gameKey);
        try {
            Bukkit.getPluginManager().callEvent(gameStart);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        new PlayGameRunnable(gameKey, Objects.requireNonNull(GetGame(gameKey)).props.gameLength).run();
    }

    private static void HandlePlayerLeaveGame(@NonNull String gameKey, Player player) {
        player.sendMessage(MessageManager.GetMessageByKey("info.leave_game", gameKey));
        RemovePlayerFromAllGames(player.getUniqueId());
    }

    private static void HandleDisqualified(Player player) {
        player.sendMessage(MessageManager.GetMessageByKey("info.disqualified"));
        RemovePlayerFromAllGames(player.getUniqueId());
    }

    private static void HandleStartingGame(@NonNull String gameKey, int timeUntilStart) {
        new StartGameRunnable(gameKey, timeUntilStart).run();
    }
}
