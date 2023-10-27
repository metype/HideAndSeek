package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.PluginStorage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GameCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) return true;
        if(args[0].equalsIgnoreCase("start")) {
            if(args.length == 1) return true;
            PluginStorage.isGameRunning = true;
            PluginStorage.currentGameID = args[1];
        }
        return false;
    }
}
