package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SaveCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        boolean hasPermission = (sender instanceof ConsoleCommandSender);
        if(!hasPermission) {
            hasPermission = sender.hasPermission("hns.save");
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
