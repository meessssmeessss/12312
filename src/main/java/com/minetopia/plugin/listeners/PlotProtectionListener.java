package com.minetopia.plugin.listeners;

import com.minetopia.plugin.MinetopiaPlugin;
import com.minetopia.plugin.models.Plot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlotProtectionListener implements Listener {

    private final MinetopiaPlugin plugin;

    public PlotProtectionListener(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Plot plot = plugin.getPlotManager().getPlotAt(event.getBlock().getLocation());
        if (plot == null) return;
        Player player = event.getPlayer();
        if (!plot.hasAccess(player.getUniqueId()) && !player.hasPermission("minetopia.admin")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Je mag hier niet bouwen, dit is andermans plot.");
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Plot plot = plugin.getPlotManager().getPlotAt(event.getBlock().getLocation());
        if (plot == null) return;
        Player player = event.getPlayer();
        if (!plot.hasAccess(player.getUniqueId()) && !player.hasPermission("minetopia.admin")) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Je mag hier niet bouwen, dit is andermans plot.");
        }
    }
}
