package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ReloadCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        boolean hasPermission = (sender instanceof ConsoleCommandSender);
        if(!hasPermission) {
            hasPermission = sender.hasPermission("hns.reload");
        }
        if(hasPermission) {
            MessageManager.Init(null);
            sender.sendMessage(MessageManager.GetMessageByKey("success.command.reload"));
        } else {
            sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
        }
        return false;
    }
}
