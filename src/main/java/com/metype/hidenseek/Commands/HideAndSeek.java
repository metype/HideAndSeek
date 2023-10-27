package com.metype.hidenseek.Commands;

import com.metype.hidenseek.Commands.SubCommands.GameCommand;
import com.metype.hidenseek.Commands.SubCommands.HelpCommand;
import com.metype.hidenseek.Commands.SubCommands.ReloadCommand;
import com.metype.hidenseek.Commands.SubCommands.SaveCommand;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;

public class HideAndSeek implements CommandExecutor {

    // This method is called, when somebody uses our command
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length < 1) {
            return (new HelpCommand()).onCommand(sender, command, label, new String[]{});
        }
        if(args[0].equalsIgnoreCase("help")) {
            return (new HelpCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if(args[0].equalsIgnoreCase("game")) {
            return (new GameCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if(args[0].equalsIgnoreCase("save")) {
            return (new SaveCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if(args[0].equalsIgnoreCase("reload")) {
            return (new ReloadCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_subcommand", args[0]));
        return true;
    }
}

//beans