package com.minetopia.plugin.listeners;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Formatteert chatberichten als: [Prefix] [Level] Naam: bericht
 */
public class ChatListener implements Listener {

    private final MinetopiaPlugin plugin;

    public ChatListener(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        String prefix = plugin.getPrefixManager().getFullPrefix(event.getPlayer());
        int level = plugin.getLevelManager().getLevel(event.getPlayer().getUniqueId());
        event.setFormat(prefix + " §7[Lvl " + level + "] §f" + event.getPlayer().getName() + "§7: §f" + event.getMessage());
    }
}
