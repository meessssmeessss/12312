package com.minetopia.plugin.listeners;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Zorgt dat geboeide spelers (JailManager) niet kunnen lopen; rondkijken blijft mogelijk.
 */
public class JailListener implements Listener {

    private final MinetopiaPlugin plugin;

    public JailListener(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (!plugin.getJailManager().isHandcuffed(event.getPlayer().getUniqueId())) return;

        // Alleen blokkeren als de speler daadwerkelijk van blok verandert (kijken blijft toegestaan)
        if (event.getFrom().getBlockX() != event.getTo().getBlockX()
                || event.getFrom().getBlockY() != event.getTo().getBlockY()
                || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) {
            event.setTo(event.getFrom());
        }
    }
}
