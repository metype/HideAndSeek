package com.metype.hidenseek.Game;

import com.metype.hidenseek.Utilities.GameManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public abstract class OutOfBoundsTimer {

        private float time;

        protected BukkitTask task;
        protected final OutOfBoundsPlayer player;
        protected final Plugin plugin;


        public OutOfBoundsTimer(float time, Plugin plugin, OutOfBoundsPlayer player) {
            this.time = time;
            this.player = player;
            this.plugin = plugin;
        }


        public abstract void count(float current);


        public final void start() {
            long period = 5L;

            task = new BukkitRunnable() {

                @Override
                public void run() {
                    time -= (period/20.0);
                    count(time);
                    if (time <= 0 || !GameManager.IsPlayerOutOfBounds(player.id)) cancel();
                }

            }.runTaskTimer(plugin, 0L, period);
        }
}
