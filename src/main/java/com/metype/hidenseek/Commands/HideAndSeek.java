package com.metype.hidenseek.Commands;

import com.metype.hidenseek.Commands.SubCommands.*;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

public class HideAndSeek implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
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
        if(args[0].equalsIgnoreCase("reset_config")) {
            return (new ResetConfigCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if(args[0].equalsIgnoreCase("join_game") || args[0].equalsIgnoreCase("join")) {
            return (new JoinGameCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if(args[0].equalsIgnoreCase("leave_game") || args[0].equalsIgnoreCase("leave")) {
            return (new LeaveGameCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        if(args[0].equalsIgnoreCase("set_bounds")) {
            return (new SetBoundsCommand()).onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        }
        sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_subcommand", args[0]));
        return true;
    }
}

//beans, so many beans