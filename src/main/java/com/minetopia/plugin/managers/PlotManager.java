package com.minetopia.plugin.managers;

import com.minetopia.plugin.models.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Beheert alle plots op de server. Elke speler kan tijdelijk twee posities
 * selecteren (/plot pos1, /plot pos2) en daarmee een plot aanmaken.
 * Data wordt opgeslagen in plots.yml.
 */
public class PlotManager {

    private final JavaPlugin plugin;
    private final File file;
    private final FileConfiguration config;

    private final Map<String, Plot> plots = new HashMap<>();
    private final Map<UUID, Location> pos1Selection = new HashMap<>();
    private final Map<UUID, Location> pos2Selection = new HashMap<>();

    public PlotManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "plots.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Kon plots.yml niet aanmaken: " + e.getMessage());
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (!config.isConfigurationSection("plots")) return;
        for (String id : config.getConfigurationSection("plots").getKeys(false)) {
            String path = "plots." + id + ".";
            UUID owner = UUID.fromString(config.getString(path + "owner"));
            String world = config.getString(path + "world");
            int minX = config.getInt(path + "minX");
            int minY = config.getInt(path + "minY");
            int minZ = config.getInt(path + "minZ");
            int maxX = config.getInt(path + "maxX");
            int maxY = config.getInt(path + "maxY");
            int maxZ = config.getInt(path + "maxZ");
            int level = config.getInt(path + "level", 1);
            Plot plot = new Plot(id, owner, world, minX, minY, minZ, maxX, maxY, maxZ, level);
            for (String memberStr : config.getStringList(path + "members")) {
                plot.addMember(UUID.fromString(memberStr));
            }
            plots.put(id, plot);
        }
    }

    public void saveAll() {
        for (Plot plot : plots.values()) {
            String path = "plots." + plot.getId() + ".";
            config.set(path + "owner", plot.getOwner().toString());
            config.set(path + "world", plot.getWorld());
            config.set(path + "minX", plot.getMinX());
            config.set(path + "minY", plot.getMinY());
            config.set(path + "minZ", plot.getMinZ());
            config.set(path + "maxX", plot.getMaxX());
            config.set(path + "maxY", plot.getMaxY());
            config.set(path + "maxZ", plot.getMaxZ());
            config.set(path + "level", plot.getLevel());
            List<String> members = new ArrayList<>();
            for (UUID m : plot.getMembers()) members.add(m.toString());
            config.set(path + "members", members);
        }
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Kon plots.yml niet opslaan: " + e.getMessage());
        }
    }

    public void setPos1(UUID uuid, Location loc) { pos1Selection.put(uuid, loc); }
    public void setPos2(UUID uuid, Location loc) { pos2Selection.put(uuid, loc); }
    public Location getPos1(UUID uuid) { return pos1Selection.get(uuid); }
    public Location getPos2(UUID uuid) { return pos2Selection.get(uuid); }

    public Plot createPlot(String id, UUID owner, Location pos1, Location pos2) {
        Plot plot = new Plot(id, owner, pos1, pos2);
        plots.put(id, plot);
        return plot;
    }

    public boolean deletePlot(String id) {
        return plots.remove(id) != null;
    }

    public Plot getPlot(String id) {
        return plots.get(id);
    }

    public Plot getPlotAt(Location loc) {
        for (Plot plot : plots.values()) {
            if (plot.contains(loc)) return plot;
        }
        return null;
    }

    public List<Plot> getPlotsOf(UUID owner) {
        List<Plot> result = new ArrayList<>();
        for (Plot plot : plots.values()) {
            if (plot.getOwner().equals(owner)) result.add(plot);
        }
        return result;
    }

    public Collection<Plot> getAllPlots() {
        return plots.values();
    }
}
