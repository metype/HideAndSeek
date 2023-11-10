package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Errors.PlayerJoinGameError;
import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class JoinGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.players_only"));
            return false;
        }
        if(args.length == 0) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
            return false;
        }
        PlayerJoinGameError err = GameManager.PutPlayerInGame(args[0], p.getUniqueId());
        switch (err) {
            case PlayerAlreadyInGame -> {
                sender.sendMessage(MessageManager.GetMessageByKey("error.already_in_game"));
                return false;
            }
            case GameInProgress -> {
                sender.sendMessage(MessageManager.GetMessageByKey("error.game.in_progress"));
                return false;
            }
            case GameInactive -> {
                sender.sendMessage(MessageManager.GetMessageByKey("error.game.inactive"));
                return false;
            }
            case GameDoesNotExist -> {
                sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist"));
                return false;
            }
            case Okay -> {
                Game game = GameManager.GetGame(args[0]);
                assert game != null;
                sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.join", game.gameName));
                return false;
            }
        }
        return false;
    }
}
