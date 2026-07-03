package com.minetopia.plugin.models;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Een rechthoekig plot tussen twee hoekpunten, met een eigenaar en leden
 * die er ook mogen bouwen.
 */
public class Plot {

    private final String id;
    private final UUID owner;
    private final String world;
    private final int minX, minY, minZ, maxX, maxY, maxZ;
    private final Set<UUID> members = new HashSet<>();
    private int level = 1;

    public Plot(String id, UUID owner, Location pos1, Location pos2) {
        this.id = id;
        this.owner = owner;
        this.world = pos1.getWorld().getName();
        this.minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        this.minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        this.minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        this.maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        this.maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        this.maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());
    }

    // Constructor voor het herladen vanuit opslag
    public Plot(String id, UUID owner, String world, int minX, int minY, int minZ,
                int maxX, int maxY, int maxZ, int level) {
        this.id = id;
        this.owner = owner;
        this.world = world;
        this.minX = minX; this.minY = minY; this.minZ = minZ;
        this.maxX = maxX; this.maxY = maxY; this.maxZ = maxZ;
        this.level = level;
    }

    public boolean contains(Location loc) {
        World w = loc.getWorld();
        if (w == null || !w.getName().equals(world)) return false;
        int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    public boolean hasAccess(UUID uuid) {
        return owner.equals(uuid) || members.contains(uuid);
    }

    public void addMember(UUID uuid) { members.add(uuid); }
    public void removeMember(UUID uuid) { members.remove(uuid); }

    public String getId() { return id; }
    public UUID getOwner() { return owner; }
    public String getWorld() { return world; }
    public int getMinX() { return minX; }
    public int getMinY() { return minY; }
    public int getMinZ() { return minZ; }
    public int getMaxX() { return maxX; }
    public int getMaxY() { return maxY; }
    public int getMaxZ() { return maxZ; }
    public Set<UUID> getMembers() { return members; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
