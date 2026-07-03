package com.minetopia.plugin.listeners;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

/**
 * Verkoop-interactie: rechtsklik de dealer-NPC (villager "Man met Bivakmuts")
 * met Verwerkte Wiet in je hand om te verkopen.
 */
public class DealerListener implements Listener {

    private final MinetopiaPlugin plugin;

    public DealerListener(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (!(entity instanceof Villager villager)) return;
        if (villager.getCustomName() == null
                || !ChatColor.stripColor(villager.getCustomName()).equals("Man met Bivakmuts")) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        plugin.getWietManager().verkoopAanDealer(player);
    }
}
