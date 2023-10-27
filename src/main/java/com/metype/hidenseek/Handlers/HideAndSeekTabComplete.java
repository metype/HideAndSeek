package com.metype.hidenseek.Handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HideAndSeekTabComplete implements TabCompleter {



    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> list = new ArrayList<>();
        if (sender instanceof Player) {
            if (args.length == 0) {
                list.add("game");
                list.add("save");
                list.add("help");
                list.add("reload");
                Collections.sort(list);
                return list;
            } else if (args.length == 1) {
                list.add("game");
                list.add("save");
                list.add("help");
                list.add("reload");
                list.removeIf(s -> !s.toLowerCase().contains(args[0].toLowerCase()));
                Collections.sort(list);
                return list;
            }
        }
        return null;
    }
}
