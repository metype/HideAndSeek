package com.metype.hidenseek.Handlers;

import com.metype.hidenseek.PluginStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import java.util.Objects;
import java.util.logging.Level;

public class HideAndSeekEventHandler implements Listener {

    private Plugin thisPlugin;

    public HideAndSeekEventHandler(Plugin p){
        thisPlugin = p;
    }
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if(!PluginStorage.isGameRunning) return;
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player whoWasHit = (Player) e.getEntity();
            Player whoHit = (Player) e.getDamager();
            thisPlugin.getLogger().log(Level.INFO, whoWasHit.getDisplayName() + " was hit by " + whoHit.getDisplayName());

            Scoreboard sb = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
            Team hidersTeam = null, seekersTeam = null;
            for (Team team : sb.getTeams()) {
                if(team.getName().equalsIgnoreCase("hiders")) {
                    hidersTeam = team;
                }
                if(team.getName().equalsIgnoreCase("seekers")) {
                    seekersTeam = team;
                }
            }
            if (hidersTeam.hasEntry(whoWasHit.getName()) && seekersTeam.hasEntry(whoHit.getName())) {
                whoWasHit.playSound(whoWasHit.getLocation(), "minecraft:entity.arrow.hit_player", 1, 1);
                hidersTeam.removeEntry(whoWasHit.getName());
                seekersTeam.addEntry(whoWasHit.getName());
                PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, 20, 2, true, true);
                whoWasHit.addPotionEffect(speedEffect);
            }
        }
    }

    @EventHandler
    public void onEffectEnd(EntityPotionEffectEvent e) {
        if(!PluginStorage.isGameRunning) return;
        if(e.getCause() == EntityPotionEffectEvent.Cause.EXPIRATION) {
            Scoreboard sb = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
            Team hidersTeam = null, seekersTeam = null;
            for (Team team : sb.getTeams()) {
                if(team.getName().equalsIgnoreCase("hiders")) {
                    hidersTeam = team;
                }
                if(team.getName().equalsIgnoreCase("seekers")) {
                    seekersTeam = team;
                }
            }

            for(Player player : thisPlugin.getServer().getOnlinePlayers()) {
                if(seekersTeam.hasEntry(player.getName())) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 7, 2));
                }
            }
        }
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.PHYSICAL) {
            if(e.getPlayer().getLocation().toVector().isInAABB(new Vector(-85, 96, -105), new Vector(-82, 96, -104))) {
                //logic for standing on pressure plate
                e.getPlayer().sendMessage("Hello!");
            }
        }

    }
}
