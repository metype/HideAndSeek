package com.metype.hidenseek.Handlers;

import com.metype.hidenseek.Game.Game;
import com.metype.hidenseek.Game.OutOfBoundsPlayer;
import com.metype.hidenseek.Game.OutOfBoundsTimer;
import com.metype.hidenseek.Game.Polygon;
import com.metype.hidenseek.Utilities.BoundsEditingPlayer;
import com.metype.hidenseek.Utilities.GameManager;
import com.metype.hidenseek.Utilities.MessageManager;
import com.metype.hidenseek.Utilities.PluginStorage;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class HideAndSeekEventHandler implements Listener {

    private final Plugin plugin;
    private Team hidersTeam, seekersTeam;

    public HideAndSeekEventHandler(Plugin p) {
        plugin = p;
        Scoreboard mainScoreboard = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        hidersTeam = null;
        seekersTeam = null;
        for (Team team : mainScoreboard.getTeams()) {
            if(team.getName().equalsIgnoreCase("hiders")) {
                hidersTeam = team;
            }
            if(team.getName().equalsIgnoreCase("seekers")) {
                seekersTeam = team;
            }
        }
        if(hidersTeam == null) {
            hidersTeam = mainScoreboard.registerNewTeam("hiders");
        }
        if(seekersTeam == null) {
            seekersTeam = mainScoreboard.registerNewTeam("seekers");
        }
        hidersTeam.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        hidersTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.FOR_OTHER_TEAMS);
    }

    @EventHandler
    public void OnGameStart(GameStartEvent e) {
        Game game = GameManager.GetGame(e.getGameKey());
        if(game == null) return;
        if(game.players.size() < 2) {
            plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_cancelled_not_enough_players", game.gameName));
            e.setCancelled(true);
            return;
        }
        plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_starting", Objects.requireNonNull(GameManager.GetGame(e.getGameKey())).gameName));
        AssignTeams(game);
        game.oobPlayers.clear();
    }

    @EventHandler
    public void OnGameEnd(GameEndEvent e) {
        Game game = GameManager.GetGame(e.getGameKey());
        if(game == null) return;
        for(UUID id : game.players) {
            Player player = Bukkit.getPlayer(id);
            if(player == null) continue;
            hidersTeam.removeEntry(player.getName());
            seekersTeam.removeEntry(player.getName());
        }
    }

    @EventHandler
    public void OnPlayerLeaveGame(PlayerLeaveGameEvent e) {
        if(e.getReason() == PlayerLeaveGameReason.GAME_END) return;
        Game game = GameManager.GetGame(e.getGameKey());
        if(game == null) return;
        if(game.hiders.isEmpty() || game.players.size() == game.hiders.size()) {
            LetGameEnd(e.getGameKey());
        }
    }

    private void DrawPolygon(Player p, Polygon poly) {
        for (int i = 0; i < poly.points.size(); i++) {
            double x = poly.points.get(i).x;
            double z = poly.points.get(i).y;
            double x2 = poly.points.get((i+1)%poly.points.size()).x;
            double z2 = poly.points.get((i+1)%poly.points.size()).y;
            double deltaX = x2 - x; // get the x-difference between the points.
            double deltaZ = z2 - z; // get the z-difference between the points.
            double distance = Math.sqrt((x2 - x) * (x2 - x) + (z2 - z) * (z2 - z));
            for (double d = 0; d < 1; d += 0.5/distance) {
                p.spawnParticle(Particle.FLAME, new Location(p.getWorld(), x + deltaX * d, p.getLocation().getBlockY(), z + deltaZ * d), 0);
            }
        }
    }

    private void AssignTeams(@NonNull Game game) {
        Random rng = new Random();
        for(int i=0; i<Math.min(game.props.startingSeekers, game.players.size()-1); i++){
            Player player = Bukkit.getPlayer(game.players.get(rng.nextInt(game.players.size())));
            if(player == null) continue;

            if(game.seekers.contains(player.getUniqueId())) {
                i--;
                continue;
            }
            game.seekers.add(player.getUniqueId());
            seekersTeam.addEntry(player.getName());
            player.sendTitle(MessageManager.GetMessageByKey("info.joined_seekers"), "", 10, 40, 10);
        }
        for(UUID id : game.players) {
            Player player = Bukkit.getPlayer(id);
            if(player == null) continue;

            if(game.seekers.contains(id)) {
                continue;
            }
            game.hiders.add(id);
            hidersTeam.addEntry(player.getName());
            player.sendTitle(MessageManager.GetMessageByKey("info.joined_hiders"), "", 10, 40, 10);
        }
    }

    private void LetGameEnd(@NonNull String gameKey) {
        Game game = GameManager.GetGame(gameKey);
        assert game != null;

        if(GameManager.IsGameStarting(gameKey)) return;
        if(game.hasEnded) return;
        game.hasEnded = true;

        if(game.hiders.size() != 1) {
            plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_no_winner", game.gameName));
        } else {
            Player winningPlayer = plugin.getServer().getPlayer(game.hiders.get(0));

            assert winningPlayer != null;

            plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_win", game.gameName, winningPlayer.getName()));
        }

        if(game.props.autoNewGame) {
            plugin.getServer().broadcastMessage(MessageManager.GetMessageByKey("broadcast.game_auto_restart", game.gameName, ""+game.props.autoNewGameStartTime));
            GameManager.ResetGame(gameKey);

            Runnable startGameDelay = () -> GameManager.RestartGame(gameKey);
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.schedule(startGameDelay, game.props.autoNewGameStartTime, TimeUnit.SECONDS);
        }
    }

    @EventHandler
    public void OnHit(EntityDamageByEntityEvent e) {
        if(!(e.getEntity() instanceof Player && e.getDamager() instanceof Player)) return;
        if(!GameManager.IsPlayerInAnyGame(e.getEntity().getUniqueId())) return;

        Game game = GameManager.GetGame(e.getEntity().getUniqueId());
        Game damagerGame = GameManager.GetGame(e.getDamager().getUniqueId());

        if(game != damagerGame || game == null) {
            return;
        }

        e.setCancelled(true);

        if (e.getEntity() instanceof Player whoWasHit && e.getDamager() instanceof Player whoHit) {
            if (game.hiders.contains(whoWasHit.getUniqueId()) && game.seekers.contains(whoHit.getUniqueId())) {
                if(whoHit.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                    return;
                }
                whoWasHit.playSound(whoWasHit.getLocation(), "minecraft:entity.arrow.hit_player", 1, 1);
                hidersTeam.removeEntry(whoWasHit.getName());
                seekersTeam.addEntry(whoWasHit.getName());

                if(game.hiders.size() <= 1) {
                    String gameKey = GameManager.GetKey(game);
                    assert gameKey != null;
                    LetGameEnd(gameKey);
                    return;
                }

                game.hiders.remove(whoWasHit.getUniqueId());
                game.seekers.add(whoWasHit.getUniqueId());

                for(UUID pl : game.hiders) {
                    Player p = Bukkit.getPlayer(pl);
                    if(p == null) continue;
                    p.sendMessage(MessageManager.GetMessageByKey("info.player_hit", whoWasHit.getName(), String.valueOf(game.hiders.size())));
                }

                whoWasHit.sendMessage(MessageManager.GetMessageByKey("info.joined_seekers_chat"));
                whoWasHit.sendTitle(MessageManager.GetMessageByKey("info.joined_seekers_title"), "", 10, 40, 10);
                if(game.props.seekerSpeedStrength > 0) {
                    PotionEffect speedEffect = new PotionEffect(PotionEffectType.SPEED, 20, game.props.seekerSpeedStrength, true, true);
                    whoWasHit.addPotionEffect(speedEffect);
                }
            }
        }
    }

    @EventHandler
    public void OnProjectileLaunch(ProjectileLaunchEvent e) {
        if(e.getEntity().getShooter() instanceof Player shooter) {
            if(e.getEntity() instanceof EnderPearl pearl) {
                Game game = GameManager.GetGame(shooter.getUniqueId());
                if(game == null) return;
                if(!game.props.allowEnderPearls) {
                    e.setCancelled(true);
                    shooter.sendMessage(MessageManager.GetMessageByKey("info.game.ender_pearls_disabled", game.gameName));
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
                e.getPlayer().sendMessage(MessageManager.GetMessageByKey("info.game.chorus_fruit_disabled", game.gameName));
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
                    player.sendMessage(MessageManager.GetMessageByKey("info.game.elytra_disabled", game.gameName));
                }
            }
        }
    }

    @EventHandler
    public void OnEntityDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player player) {
            Game game = GameManager.GetGame(player.getUniqueId());
            if(game == null) return;
            if(!game.props.allowDamage) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void OnEffectEnd(EntityPotionEffectEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        if(!GameManager.IsPlayerInAnyGame(e.getEntity().getUniqueId())) return;

        Game game = GameManager.GetGame(e.getEntity().getUniqueId());

        if(game == null) return;

        if(game.props.seekerSpeedStrength <= 0) return;

        if(e.getCause() == EntityPotionEffectEvent.Cause.EXPIRATION) {
            for(Player player : plugin.getServer().getOnlinePlayers()) {
                if(seekersTeam.hasEntry(player.getName())) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 7*20, game.props.seekerSpeedStrength));
                }
            }
        }
    }

    @EventHandler
    public void OnPlayerInteract(PlayerInteractEvent e) {
        BoundsEditingPlayer player = PluginStorage.GetBoundsEditingPlayer(e.getPlayer().getUniqueId());
        if(player != null) {
            Game game = GameManager.GetGame(player.gameKey);
            if (game == null) {
                PluginStorage.PlayerStopEditingGameBounds(player.id);
                return;
            }
            int heldItemSlot = e.getPlayer().getInventory().getHeldItemSlot();

            ItemStack heldItem = e.getPlayer().getInventory().getItem(heldItemSlot);

            if (heldItem == null) return;

            if (heldItem.getType() == Material.GOLDEN_HOE) {
                e.setCancelled(true);
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {

                    Location blockPos = Objects.requireNonNull(e.getClickedBlock()).getLocation();

                    Polygon.Point additionalPoint = new Polygon.Point(blockPos.getBlockX(), blockPos.getBlockZ());

                    if (game.gameBounds.points.contains(additionalPoint)) {
                        return;
                    }

                    game.gameBounds.addPoint(additionalPoint);
                    e.getPlayer().sendMessage(MessageManager.GetMessageByKey("info.bounds.add_point", additionalPoint.toString()));
                    if (game.gameBounds.points.size() < 3) return;

                    DrawPolygon(e.getPlayer(), game.gameBounds);
                }
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Location blockPos = Objects.requireNonNull(e.getClickedBlock()).getLocation();
                    Polygon.Point additionalPoint = new Polygon.Point(blockPos.getBlockX(), blockPos.getBlockZ());

                    if (game.gameBounds.points.contains(additionalPoint)) {
                        game.gameBounds.points.remove(additionalPoint);
                        e.getPlayer().sendMessage(MessageManager.GetMessageByKey("info.bounds.remove_point", additionalPoint.toString()));
                    }
                }
            }
        } else {

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
        if(!GameManager.IsPlayerInAnyGame(player.getUniqueId())) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void OnPlayerLogout(PlayerQuitEvent e) {
        PluginStorage.PlayerStopEditingGameBounds(e.getPlayer().getUniqueId());
        GameManager.RemovePlayerFromAllGames(e.getPlayer().getUniqueId(), PlayerLeaveGameReason.LEFT_GAME);
    }

    @EventHandler
    public void OnPlayerMove(PlayerMoveEvent e) {
        if(PluginStorage.PlayerIsEditingGameBounds(e.getPlayer().getUniqueId())) {
            BoundsEditingPlayer player = PluginStorage.GetBoundsEditingPlayer(e.getPlayer().getUniqueId());
            assert player != null;
            Game game = GameManager.GetGame(player.gameKey);
            if(game == null) {
                PluginStorage.PlayerStopEditingGameBounds(player.id);
                return;
            }
            if(game.gameBounds.points.size() < 3) return;

            DrawPolygon(e.getPlayer(), game.gameBounds);
        }

        if(!GameManager.IsPlayerInAnyGame(e.getPlayer().getUniqueId())) return;
        Game game = GameManager.GetGame(e.getPlayer().getUniqueId());

        if(game == null) return;

        if(e.getPlayer().hasPotionEffect(PotionEffectType.BLINDNESS) && game.seekers.contains(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        boolean couldBeOOB = game.props.forceHidersInBounds && game.hiders.contains(e.getPlayer().getUniqueId());
        couldBeOOB |= game.props.forceSeekersInBounds && game.seekers.contains(e.getPlayer().getUniqueId());

        if(!couldBeOOB) return;

        Location l = e.getPlayer().getLocation();

        boolean isNotInBounds = !game.gameBounds.checkInside(new Polygon.Point(l.getBlockX(), l.getBlockZ()));

        isNotInBounds |= game.props.maxHeightBounds < l.getBlockY();
        isNotInBounds |= game.props.minHeightBounds > l.getBlockY();

        if(isNotInBounds) {
            for (OutOfBoundsPlayer pl: game.oobPlayers) {
                if(pl.id == e.getPlayer().getUniqueId()) {
                    return;
                }
            }
            OutOfBoundsPlayer pl = new OutOfBoundsPlayer(e.getPlayer().getUniqueId(), System.currentTimeMillis());
            game.oobPlayers.add(pl);
            new OutOfBoundsTimer(game.props.outOfBoundsTime, plugin, pl) {

                @Override
                public void count(float current) {
                    Player player = this.plugin.getServer().getPlayer(this.player.id);
                    if(player == null) return;
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(MessageManager.GetMessageByKey("info.out_of_bounds", current)));
                    if(current < 0.25) {
                        GameManager.DisqualifyPlayer(player.getUniqueId());
                    }
                }

            }.start();
        }
        if(!isNotInBounds) {
            game.oobPlayers.removeIf((player) -> player.id == e.getPlayer().getUniqueId());
        }
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
