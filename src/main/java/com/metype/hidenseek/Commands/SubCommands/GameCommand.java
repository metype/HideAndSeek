package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Game;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        StringBuilder argsString = new StringBuilder();
        for(String arg : args) {
            argsString.append(arg).append(" ");
        }
        List<String> newArgs = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(argsString);
        while (m.find())
            newArgs.add(m.group(1).replace("\"", ""));

        args = newArgs.toArray(args);

        if(args.length == 0) return true;
        if(args[0].equalsIgnoreCase("start")) {
            if(!sender.hasPermission("hns.game.start")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
            }
            if(args.length < 3) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return StartGame(sender, args[1], args[2]);
        }
        if(args[0].equalsIgnoreCase("create")) {
            if(!sender.hasPermission("hns.game.create")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length <= 2) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return CreateGame(sender, args[1], args[2]);
        }
        if(args[0].equalsIgnoreCase("edit")) {
            if(!sender.hasPermission("hns.game.edit")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length <= 2) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return CreateGame(sender, args[1], args[2]);
        }
        return false;
    }

    private boolean StartGame(CommandSender sender, String gameKey, String timeStr) {
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.start", gameKey, timeStr));
        return true;
    }

    private boolean CreateGame(CommandSender sender, String gameKey, String gameName) {
        if(!gameKey.replaceAll("([\\\0-/]|[:-@]|[\\[-`]|[{-\\\177])", "_").equals(gameKey)) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.command.game.create.bad_key", gameKey));
        }
        Game newGame = new Game();
        newGame.gameName = gameName;

        GameManager.NewGame(gameKey, newGame);
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.create", gameKey, newGame.gameName));
        return true;
    }

    private boolean EditGame(CommandSender sender, String gameKey, String argName, String argValue) {
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        return true;
    }
}
