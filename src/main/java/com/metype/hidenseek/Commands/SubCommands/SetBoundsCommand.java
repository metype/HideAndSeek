package com.metype.hidenseek.Commands.SubCommands;

import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Game.Polygon;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import com.metype.hidenseek.Utilities.PluginStorage;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SetBoundsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        if(!(sender instanceof Player player)) {
            return true;
        }

        boolean hasPermission = sender.hasPermission("hns.set_bounds");
        if(hasPermission) {
            if(PluginStorage.GetBoundsEditingPlayer(player.getUniqueId()) != null) {
                PluginStorage.PlayerStopEditingGameBounds(player.getUniqueId());
                sender.sendMessage(MessageManager.GetMessageByKey("success.command.edit_bounds.cancel"));
                return true;
            }
            if(args.length < 1) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.not_enough_args"));
                return true;
            }

            Game editedGame = GameManager.GetGame(args[0]);
            if(editedGame == null) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.game.no_exist"));
                return true;
            }

            boolean hasFreeSlot = false;

            for(ItemStack item : player.getInventory().getContents()){
                if(item == null) {
                    hasFreeSlot = true;
                    break;
                }
                if(item.getType() == Material.AIR){
                    hasFreeSlot= true;
                    break;
                }
            }

            if(!hasFreeSlot) {
                sender.sendMessage(MessageManager.GetMessageByKey("error.inventory.no_free_slot"));
                return false;
            }

            editedGame.gameBounds = new Polygon(0);
            PluginStorage.PlayerStartEditingGameBounds(args[0], player.getUniqueId());

            ItemStack editTool = new ItemStack(Material.GOLDEN_HOE);
            ItemMeta editToolMeta = editTool.getItemMeta();
            if(editToolMeta == null) {
                player.sendMessage("no");
                return false;
            }
            editToolMeta.setDisplayName("§6Bounds Edit Tool");
            editTool.setItemMeta(editToolMeta);
            player.getInventory().addItem(editTool);

            sender.sendMessage(MessageManager.GetMessageByKey("success.command.edit_bounds", args[0]));
        } else {
            sender.sendMessage(MessageManager.GetMessageByKey("error.no_permission"));
        }
        return false;
    }
}
