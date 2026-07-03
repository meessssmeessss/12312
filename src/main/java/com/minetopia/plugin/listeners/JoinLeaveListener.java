package com.minetopia.plugin.listeners;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinLeaveListener implements Listener {

    private final MinetopiaPlugin plugin;

    public JoinLeaveListener(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // Zorgt dat de speler direct een balans/level heeft en het scoreboard meteen ziet.
        plugin.getEconomyManager().getBalance(event.getPlayer().getUniqueId());
        plugin.getLevelManager().getLevel(event.getPlayer().getUniqueId());
        plugin.getScoreboardManager().updateScoreboard(event.getPlayer());
    }
}
