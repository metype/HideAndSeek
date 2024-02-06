package com.metype.hidenseek.Handlers;

import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import com.metype.hidenseek.Utilities.PluginStorage;
import com.metype.hidenseek.Utilities.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class DisabledEventsHandler implements Listener {

    @EventHandler
    public void OnPlayerTeleport(PlayerTeleportEvent e) {
        Game game = GameManager.GetGame(e.getPlayer().getUniqueId());
        if(game == null) return;
        if(!game.props.allowTeleport && !game.playersThatCanTeleport.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            StringUtils.ShowActionBarText(e.getPlayer(), MessageManager.GetMessageByKey("info.game.teleport_disabled", game.props.gameName), 1, 3);
        }
        game.playersThatCanTeleport.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void OnProjectileLaunch(ProjectileLaunchEvent e) {
        if(e.getEntity().getShooter() instanceof Player shooter) {
            Game game = GameManager.GetGame(shooter.getUniqueId());
            if(game == null) return;
            if(e.getEntity() instanceof EnderPearl) {
                if(!game.props.allowEnderPearls) {
                    e.setCancelled(true);
                    StringUtils.ShowActionBarText(shooter, MessageManager.GetMessageByKey("info.game.ender_pearls_disabled", game.props.gameName), 1, 2);
                }
            }
            if(e.getEntity() instanceof Arrow) {
                if(!game.props.allowShootBows) {
                    e.setCancelled(true);
                    StringUtils.ShowActionBarText(shooter, MessageManager.GetMessageByKey("info.game.shoot_bows_disabled", game.props.gameName), 1, 2);
                }
            }
            if(e.getEntity() instanceof Snowball) {
                if(!game.props.allowThrowSnowballs) {
                    e.setCancelled(true);
                    StringUtils.ShowActionBarText(shooter, MessageManager.GetMessageByKey("info.game.snowball_disabled", game.props.gameName), 1, 2);
                }
            }
            if(e.getEntity() instanceof Egg) {
                if(!game.props.allowThrowEggs) {
                    e.setCancelled(true);
                    StringUtils.ShowActionBarText(shooter, MessageManager.GetMessageByKey("info.game.eggs_disabled", game.props.gameName), 1, 2);
                }
            }
        }
    }

    @EventHandler
    public void OnPlayerEat(PlayerItemConsumeEvent e) {
        if(e.getItem().getType() == Material.CHORUS_FRUIT) {
            Game game = GameManager.GetGame(e.getPlayer().getUniqueId());
            if(game == null) return;
            if(!game.props.allowChorusFruit) {
                e.setCancelled(true);
                StringUtils.ShowActionBarText(e.getPlayer(), MessageManager.GetMessageByKey("info.game.chorus_fruit_disabled", game.props.gameName), 1, 2);
            }
        }
    }

    @EventHandler
    public void OnEntityToggleGlide(EntityToggleGlideEvent e) {
        if(e.getEntity() instanceof Player player) {
            if(e.isGliding()) {
                Game game = GameManager.GetGame(player.getUniqueId());
                if(game == null) return;
                if(!game.props.allowElytra) {
                    e.setCancelled(true);
                    StringUtils.ShowActionBarText(player, MessageManager.GetMessageByKey("info.game.elytra_disabled", game.props.gameName), 1, 2);
                }
            }
        }
    }

    @EventHandler
    public void OnEntityDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player player) {
            Game game = GameManager.GetGame(player.getUniqueId());
            if(game == null) return;
            e.setCancelled(!game.props.allowDamage);
        }
    }

    @EventHandler
    public void OnProjectileDamage(ProjectileHitEvent e) {
        if(e.getEntity() instanceof Player player) {
            Game game = GameManager.GetGame(player.getUniqueId());
            if(game == null) return;
            e.setCancelled(!game.props.allowDamage);
        }
    }

    @EventHandler
    public void OnSignChange(SignChangeEvent e) {
        Game game = GameManager.GetGame(e.getPlayer().getUniqueId());
        if (game == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void OnOpenInventory(InventoryOpenEvent e){
        if(e.getInventory().getType() == InventoryType.PLAYER
                || e.getInventory().getType() == InventoryType.ENDER_CHEST) return;
        if(!(e.getPlayer() instanceof Player player)) return;
        Game game = GameManager.GetGame(player.getUniqueId());
        if(game == null) return;
        if(PluginStorage.playersInHNSUI.contains(player.getUniqueId())) return;
        if(game.props.allowOpeningContainers) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void OnBlockPlace(BlockPlaceEvent e) {
        if(!GameManager.IsPlayerInAnyGame(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void OnBlockBreak(BlockBreakEvent e) {
        if(!GameManager.IsPlayerInAnyGame(e.getPlayer().getUniqueId())) return;
        e.setCancelled(true);
    }
}
