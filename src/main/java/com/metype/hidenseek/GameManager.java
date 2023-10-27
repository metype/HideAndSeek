package com.metype.hidenseek;

import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.metype.hidenseek.Deserializers.GameDeserializer;
import com.metype.hidenseek.Serializers.GameSerializer;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import com.google.gson.Gson;

public class GameManager {
    private static List<Game> games;
    private static Plugin plugin;

    public static void Init(Plugin p) {
        games = new ArrayList<>();
        if(p != null)
            plugin = p;
        assert plugin != null;
        File configDir = new File(plugin.getDataFolder(), "games");
        if(!configDir.exists()) {
            if(!configDir.mkdirs()) {
                plugin.getLogger().log(Level.SEVERE, "Cannot create the games directory to read/write game data to, must disable.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
        }

        FilenameFilter filter = (f, name) -> {
            // Matches any filename with the .json extention
            return name.matches(".*\\.json");
        };

        for(File gameFile : Objects.requireNonNull(configDir.listFiles(filter))) {
            try {
                Gson file = new GsonBuilder()
                        .registerTypeAdapter(Game.class, new GameDeserializer())
                        .create();
                StringBuilder fileContents = new StringBuilder();
                for(String line : Files.readLines(gameFile, Charset.defaultCharset())) {
                    fileContents.append(line).append("\n");
                }
                Game game = file.fromJson(fileContents.toString(), Game.class);
                games.add(game);
            } catch(IOException ignored) {
                // If there's an IOException there's nothing we can really do
                plugin.getLogger().log(Level.WARNING, "Cannot read the contents of " + gameFile.getAbsolutePath() + "!");
            }
        }
        Game game = new Game();
        game.gameName = "Test Game";
        games.add(game);
    }

    public static void SaveGames() {
        File configDir = new File(plugin.getDataFolder(), "games");
        if(!configDir.exists()) {
            if(!configDir.mkdirs()) {
                plugin.getLogger().log(Level.SEVERE, "Cannot create the games directory to read/write game data to, must disable.");
                plugin.getServer().getPluginManager().disablePlugin(plugin);
                return;
            }
        }
        for(Game game : games){
            Gson file = new GsonBuilder()
                    .registerTypeAdapter(Game.class, new GameSerializer())
                    .create();
            String fileContents = file.toJson(game);
            String fileName = game.gameName.replaceAll("([\\\0-/]|[:-@]|[\\[-`]|[{-\\\177])", "_");
            File jsonFile = new File(configDir, fileName+".json");
            try {
                FileWriter writer = new FileWriter(jsonFile);
                writer.write(fileContents);
                writer.close();
            } catch (IOException ignored) {
                plugin.getLogger().log(Level.WARNING, "Cannot write to the contents of " + jsonFile.getAbsolutePath() + "!");
                continue;
            }
        }
    }
}
