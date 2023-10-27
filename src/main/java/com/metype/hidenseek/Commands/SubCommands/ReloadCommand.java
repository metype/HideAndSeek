package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ReloadCommand implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        boolean hasPermission = (sender instanceof ConsoleCommandSender);
        if(!hasPermission) {
            hasPermission = sender.hasPermission("hidenseek.reload");
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
