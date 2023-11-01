package com.metype.hidenseek.Utilities;

import com.google.common.io.Files;
import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class MessageManager {
    private static Map<String, String> messages = new HashMap<>();
    private static Plugin plugin;

    public static void Init(Plugin p) {
        if(p != null)
            plugin = p;
        assert plugin != null;
        File messagesFile = new File(plugin.getDataFolder(), "messages.json");
        if(!messagesFile.exists()) {
            ResetMessageConfig();
        }
    }

    public static void ResetMessageConfig() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.json");
        try {
            if(!messagesFile.createNewFile()) {
                try (FileChannel outChan = new FileOutputStream(messagesFile, true).getChannel()) {
                    outChan.truncate(0);
                }
            }
            try (InputStream in = plugin.getClass().getResourceAsStream("/messages.json")) {
                assert in != null;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                     FileWriter writer = new FileWriter(messagesFile)) {
                    writer.write(reader.lines().collect(Collectors.joining(System.lineSeparator())));
                }
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not create messages.json and one was not found!");
        }

        Gson file = new Gson();
        StringBuilder fileContents = new StringBuilder();
        try {
            for (String line : Files.readLines(messagesFile, Charset.defaultCharset())) {
                fileContents.append(line).append("\n");
            }
        } catch(IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not read the contents of messages.json, this is bad. Real bad.");
        }
        messages = file.<Map<String, String>>fromJson(fileContents.toString(), Map.class);
    }

    public static String GetMessageByKey(String key, Object... args) {
        if(!messages.containsKey(key)) return "Message \""+ key +"\" not found in messages.json, this is an error.";

        String messageData = ChatColor.translateAlternateColorCodes('&', messages.get(key));
        return String.format(messageData, args);
    }
}
