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
 * Beheert het geld van alle spelers. Data wordt opgeslagen in economy.yml.
 */
public class EconomyManager {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;
    private final Map<UUID, Double> balances = new HashMap<>();

    public static final double START_SALDO = 500.0;

    public EconomyManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "economy.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Kon economy.yml niet aanmaken: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (config.isConfigurationSection("balances")) {
            for (String key : config.getConfigurationSection("balances").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    double amount = config.getDouble("balances." + key);
                    balances.put(uuid, amount);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
            config.set("balances." + entry.getKey(), entry.getValue());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Kon economy.yml niet opslaan: " + e.getMessage());
        }
    }

    public double getBalance(UUID uuid) {
        return balances.computeIfAbsent(uuid, u -> START_SALDO);
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, Math.max(0, amount));
    }

    public void deposit(UUID uuid, double amount) {
        setBalance(uuid, getBalance(uuid) + amount);
    }

    public boolean withdraw(UUID uuid, double amount) {
        double current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    /**
     * Maakt geld over van een speler naar een ander. Retourneert false als er onvoldoende saldo is.
     */
    public boolean transfer(UUID from, UUID to, double amount) {
        if (amount <= 0) return false;
        if (!withdraw(from, amount)) return false;
        deposit(to, amount);
        return true;
    }

    public String format(double amount) {
        return String.format("€%,.2f", amount);
    }
}
