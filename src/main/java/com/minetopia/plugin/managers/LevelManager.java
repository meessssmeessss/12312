package com.minetopia.plugin.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Beheert het level van elke speler. Levels stijgen o.a. via plot-upgrades.
 * Data wordt opgeslagen in levels.yml.
 */
public class LevelManager {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<UUID, Integer> levels = new HashMap<>();

    public LevelManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "levels.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Kon levels.yml niet aanmaken: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (config.isConfigurationSection("levels")) {
            for (String key : config.getConfigurationSection("levels").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    levels.put(uuid, config.getInt("levels." + key));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Integer> entry : levels.entrySet()) {
            config.set("levels." + entry.getKey(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Kon levels.yml niet opslaan: " + e.getMessage());
        }
    }

    public int getLevel(UUID uuid) {
        return levels.computeIfAbsent(uuid, u -> 1);
    }

    public void setLevel(UUID uuid, int level) {
        levels.put(uuid, Math.max(1, level));
    }

    public void addLevel(UUID uuid, int amount) {
        setLevel(uuid, getLevel(uuid) + amount);
    }
}
