package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ResetConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        boolean hasPermission = (sender instanceof ConsoleCommandSender);
        if(!hasPermission) {
            hasPermission = sender.hasPermission("hns.reset");
        }
        if(hasPermission) {
            if(args.length < 1) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }
            if(args[0].equalsIgnoreCase("messages")) {
                MessageManager.ResetMessageConfig();
                sender.sendMessage(MessageManager.GetMessageByKey("success.command.reset.messages"));
            }
        } else {
            sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
        }
        return false;
    }
}
