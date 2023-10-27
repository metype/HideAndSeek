package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.GameManager;
import com.metype.hidenseek.PluginStorage;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SaveCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean hasPermission = (sender instanceof ConsoleCommandSender);
        if(!hasPermission) {
            hasPermission = sender.hasPermission("hidenseek.save");
        }
        if(hasPermission) {
            GameManager.SaveGames();
            sender.sendMessage(MessageManager.GetMessageByKey("success.command.save"));
        } else {
            sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
        }
        return false;
    }
}
