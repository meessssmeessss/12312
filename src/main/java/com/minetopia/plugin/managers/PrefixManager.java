package com.minetopia.plugin.managers;

import com.minetopia.plugin.MinetopiaPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.TreeMap;

/**
 * Bepaalt de prefix van een speler op basis van zijn level.
 * Spelers met de permissie "minetopia.agent" krijgen daarnaast altijd
 * de rode [Agent]-tag, ongeacht hun level-prefix.
 *
 * Pas de LEVEL_PREFIXES map hieronder aan om je eigen rangen/kleuren in te stellen.
 */
public class PrefixManager {

    private final MinetopiaPlugin plugin;

    // Minimum level -> prefix. TreeMap zodat we makkelijk de hoogste passende drempel vinden.
    private final TreeMap<Integer, String> levelPrefixes = new TreeMap<>();

    public PrefixManager(MinetopiaPlugin plugin) {
        this.plugin = plugin;
        levelPrefixes.put(1, ChatColor.GRAY + "Burger");
        levelPrefixes.put(5, ChatColor.GREEN + "Ondernemer");
        levelPrefixes.put(10, ChatColor.AQUA + "Zakenman");
        levelPrefixes.put(20, ChatColor.GOLD + "Rijkaard");
        levelPrefixes.put(35, ChatColor.LIGHT_PURPLE + "Miljonair");
        levelPrefixes.put(50, ChatColor.RED + "Legende");
    }

    /** Geeft de rang-prefix terug die past bij het huidige level van de speler. */
    public String getRankPrefix(Player player) {
        int level = plugin.getLevelManager().getLevel(player.getUniqueId());
        Integer key = levelPrefixes.floorKey(level);
        if (key == null) key = levelPrefixes.firstKey();
        return levelPrefixes.get(key);
    }

    /** Geeft true als de speler de agent-tag moet krijgen. */
    public boolean isAgent(Player player) {
        return player.hasPermission("minetopia.agent");
    }

    /** Volledige prefix zoals getoond in chat/scoreboard, bv. "[Agent] Zakenman". */
    public String getFullPrefix(Player player) {
        String rank = getRankPrefix(player);
        if (isAgent(player)) {
            return ChatColor.RED + "[Agent] " + ChatColor.RESET + rank;
        }
        return rank;
    }
}
