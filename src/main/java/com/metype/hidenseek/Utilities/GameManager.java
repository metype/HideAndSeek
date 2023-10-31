package com.metype.hidenseek.Utilities;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.metype.hidenseek.Deserializers.GameDeserializer;
import com.metype.hidenseek.Errors.PlayerJoinGameError;
import com.metype.hidenseek.Errors.PlayerLeaveGameError;
import com.metype.hidenseek.Game;
import com.metype.hidenseek.Serializers.GameSerializer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import com.google.gson.Gson;
import org.checkerframework.checker.nullness.qual.NonNull;

public class GameManager {
    private static HashMap<String, Game> games;
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
            String fileName = key.replaceAll("([\\\0-/]|[:-@]|[\\[-`]|[{-\\\177])", "_");

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

    public static PlayerJoinGameError PutPlayerInGame(@NonNull String gameKey, @NonNull UUID playerID) {
        Game game = GetGame(gameKey);
        if(game == null) return PlayerJoinGameError.GameDoesNotExist;
        if(IsPlayerInAnyGame(playerID)) return PlayerJoinGameError.PlayerAlreadyInGame;

        game.players.add(playerID);
        return PlayerJoinGameError.Okay;
    }

    public static PlayerLeaveGameError RemovePlayerFromGame(@NonNull String gameKey, @NonNull UUID playerID) {
        Game game = GetGame(gameKey);
        if(game == null) return PlayerLeaveGameError.GameDoesNotExist;
        if(!IsPlayerInAnyGame(playerID)) return PlayerLeaveGameError.PlayerNotInGame;

        game.players.remove(playerID);
        return PlayerLeaveGameError.Okay;
    }

    public static PlayerLeaveGameError RemovePlayerFromAllGames(@NonNull UUID playerID) {
        if(!IsPlayerInAnyGame(playerID)) return PlayerLeaveGameError.PlayerNotInGame;

        for(Game game : games.values()) {
            game.players.remove(playerID);
        }

        return PlayerLeaveGameError.Okay;
    }
}
