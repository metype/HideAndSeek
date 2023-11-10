package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
                return false;
            }
            if(args.length < 3) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return StartGame(sender, args[1], args[2]);
        }
        if(args[0].equalsIgnoreCase("stop")) {
            if(!sender.hasPermission("hns.game.stop")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length < 2) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return StopGame(sender, args[1]);
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
        if(args[0].equalsIgnoreCase("set_start")) {
            if(!sender.hasPermission("hns.game.set_start")) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
                return false;
            }
            if(args.length == 1) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return false;
            }

            return SetStart(sender, args[1]);
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

            if(args.length > 3) {
                return EditGame(sender, args[1], args[2], args[3]);
            } else {
                return EditGame(sender, args[1], args[2], null);
            }
        }
        return false;
    }

    private boolean StartGame(CommandSender sender, String gameKey, String timeStr) {
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        try {
            float timeVal = Float.parseFloat(timeStr.replaceAll("[^0-9.]", ""));
            String timeMult = timeStr.replaceAll("[0-9.]", "");

            char c = timeStr.charAt(0);
            if(c >= '0' && c <= '9') {
                int gameStartTime;
                if(timeMult.length() == 0) {
                    gameStartTime = (int)timeVal;
                } else {
                    gameStartTime = switch (timeMult.charAt(0)) {
                        case 's', 'S' -> (int) timeVal;
                        case 'm', 'M' -> (int) (timeVal * 60);
                        case 'h', 'H' -> (int) (timeVal * 60 * 60);
                        default -> 0;
                    };
                }

                GameManager.StartGame(gameKey, gameStartTime);
            } else {
                throw new NumberFormatException();
            }
        } catch(NumberFormatException e) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_number", gameKey));
            return false;
        }
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.start", game.gameName, timeStr));
        return true;
    }

    private boolean StopGame(CommandSender sender, String gameKey) {
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        GameManager.EndGame(gameKey);
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.stop", game.gameName));
        return true;
    }

    private boolean CreateGame(CommandSender sender, String gameKey, String gameName) {
        if(!gameKey.replaceAll("([\0-/]|[:-@]|[\\[-`]|[{-\177])", "_").equals(gameKey)) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.command.game.create.bad_key", gameKey));
        }
        Game newGame = new Game();
        newGame.gameName = gameName;

        GameManager.NewGame(gameKey, newGame);
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.create", gameKey, newGame.gameName));
        return true;
    }

    private boolean SetStart(CommandSender sender, String gameKey) {
        if(!(sender instanceof Player player)) return false;
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        game.startGameLocation = player.getLocation();
        sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.set_start", game.gameName));
        return true;
    }

    private boolean EditGame(CommandSender sender, String gameKey, String argName, String argValue) {
        Game game = GameManager.GetGame(gameKey);
        if(game == null) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist", gameKey));
            return false;
        }
        if(argValue == null) {
            try {
                sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.edit.get", game.gameName, argName, game.GetProperty(argName)));
            } catch (NoSuchFieldException e) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_property_name", argName));
            } catch (IllegalAccessException e) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_property_value", argName, null));
            }
            return true;
        }
        try {
            Object val = game.SetProperty(argName, argValue);
            sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.edit.set", game.gameName, argName, val.toString()));
        } catch (NoSuchFieldException e) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_property_name", argName));
        } catch (IllegalAccessException | NumberFormatException e) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.invalid_property_value", argName, argValue));
        }
        return true;
    }
}
