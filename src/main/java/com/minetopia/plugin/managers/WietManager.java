package com.minetopia.plugin.managers;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Beheert het wiet-systeem: kweken (via WietListener op boerenland),
 * verwerken bij een crafting table en verkopen bij de dealer-NPC.
 *
 * Ruwe blaadjes en verwerkte wiet zijn custom items (WHEAT / SUGAR resource
 * met eigen naam+lore) zodat er geen resource pack nodig is.
 */
public class WietManager {

    private final MinetopiaPlugin plugin;
    private final Set<UUID> bezigMetVerwerken = new HashSet<>();

    public static final double VERKOOPPRIJS_PER_STUK = 45.0;
    public static final int VERWERK_DUUR_SECONDEN = 5;
    public static final int BLAADJES_PER_VERWERKING = 3;

    public WietManager(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveAll() {
        // Geen persistente state nodig; items zelf zijn de "opslag" (in de inventory van de speler).
    }

    public ItemStack maakBlaadjesItem(int amount) {
        ItemStack item = new ItemStack(Material.SUGAR_CANE, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + "Wietblaadjes");
        meta.setLore(java.util.List.of(ChatColor.GRAY + "Ruw, moet nog verwerkt worden."));
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack maakVerwerkteWietItem(int amount) {
        ItemStack item = new ItemStack(Material.SUGAR, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Verwerkte Wiet");
        meta.setLore(java.util.List.of(ChatColor.GRAY + "Klaar om te verkopen bij de dealer."));
        item.setItemMeta(meta);
        return item;
    }

    public boolean isBlaadjes(ItemStack item) {
        return item != null && item.getType() == Material.SUGAR_CANE && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Wietblaadjes");
    }

    public boolean isVerwerkteWiet(ItemStack item) {
        return item != null && item.getType() == Material.SUGAR && item.hasItemMeta()
                && item.getItemMeta().hasDisplayName()
                && ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals("Verwerkte Wiet");
    }

    public boolean isBezigMetVerwerken(UUID uuid) {
        return bezigMetVerwerken.contains(uuid);
    }

    /**
     * Start het verwerkproces voor een speler bij een crafting table.
     * Na VERWERK_DUUR_SECONDEN worden BLAADJES_PER_VERWERKING blaadjes omgezet in 1 verwerkte wiet.
     */
    public void startVerwerken(Player player) {
        UUID uuid = player.getUniqueId();
        if (bezigMetVerwerken.contains(uuid)) {
            player.sendMessage(ChatColor.RED + "Je bent al aan het verwerken!");
            return;
        }
        if (!heeftGenoegBlaadjes(player, BLAADJES_PER_VERWERKING)) {
            player.sendMessage(ChatColor.RED + "Je hebt minimaal " + BLAADJES_PER_VERWERKING + " wietblaadjes nodig.");
            return;
        }
        bezigMetVerwerken.add(uuid);
        verwijderBlaadjes(player, BLAADJES_PER_VERWERKING);
        player.sendMessage(ChatColor.YELLOW + "Je begint met verwerken... (" + VERWERK_DUUR_SECONDEN + "s)");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            bezigMetVerwerken.remove(uuid);
            if (player.isOnline()) {
                player.getInventory().addItem(maakVerwerkteWietItem(1));
                player.sendMessage(ChatColor.GREEN + "Je hebt 1x Verwerkte Wiet gemaakt!");
            }
        }, VERWERK_DUUR_SECONDEN * 20L);
    }

    private boolean heeftGenoegBlaadjes(Player player, int amount) {
        int total = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (isBlaadjes(item)) total += item.getAmount();
        }
        return total >= amount;
    }

    private void verwijderBlaadjes(Player player, int amount) {
        int resterend = amount;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length && resterend > 0; i++) {
            ItemStack item = contents[i];
            if (isBlaadjes(item)) {
                int afname = Math.min(resterend, item.getAmount());
                item.setAmount(item.getAmount() - afname);
                resterend -= afname;
            }
        }
    }

    /** Verkoopt alle "Verwerkte Wiet" in de hand van de speler aan de dealer. */
    public void verkoopAanDealer(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!isVerwerkteWiet(hand)) {
            player.sendMessage(ChatColor.RED + "Je moet Verwerkte Wiet in je hand houden om te verkopen.");
            return;
        }
        int amount = hand.getAmount();
        double totaal = amount * VERKOOPPRIJS_PER_STUK;
        player.getInventory().setItemInMainHand(null);
        plugin.getEconomyManager().deposit(player.getUniqueId(), totaal);
        player.sendMessage(ChatColor.GREEN + "Je hebt " + amount + "x Verwerkte Wiet verkocht voor "
                + plugin.getEconomyManager().format(totaal) + "!");
    }
}
