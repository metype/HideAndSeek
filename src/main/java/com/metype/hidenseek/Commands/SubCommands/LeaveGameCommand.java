package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Errors.PlayerJoinGameError;
import com.metype.hidenseek.Errors.PlayerLeaveGameError;
import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LeaveGameCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(!(sender instanceof Player p)) {
            sender.sendMessage(MessageManager.GetMessageByKey("error.players_only"));
            return false;
        }
        Game game = GameManager.GetGame(p.getUniqueId());
        PlayerLeaveGameError err = GameManager.RemovePlayerFromAllGames(p.getUniqueId());
        switch (err) {
            case PlayerNotInGame -> {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_in_game"));
                return false;
            }
            case GameDoesNotExist -> {
                sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist"));
                return false;
            }
            case Okay -> {
                assert game != null;
                sender.sendMessage(MessageManager.GetMessageByKey("success.command.game.leave", game.gameName));
                return false;
            }
        }
        return false;
    }
}
