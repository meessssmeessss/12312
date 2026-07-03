package com.minetopia.plugin.listeners;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Simpel wiet-systeem zonder resource pack:
 * - Kweken: speler plant "Wietzaadje" (aangepaste NETHER_WART) op boerenland; groeit vanzelf.
 * - Oogsten: breek de volgroeide plant -> krijg Wietblaadjes.
 * - Verwerken: rechtsklik een Crafting Table met Wietblaadjes in de hand -> WietManager.startVerwerken.
 * - Verkopen: zie DealerListener.
 */
public class WietListener implements Listener {

    private final MinetopiaPlugin plugin;

    public WietListener(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Geeft een speler een startzaadje, bijvoorbeeld te verkrijgen via een shop-commando of admin-geef. */
    public static ItemStack maakZaadje() {
        ItemStack item = new ItemStack(Material.NETHER_WART);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Wietzaadje");
        item.setItemMeta(meta);
        return item;
    }

    private boolean isWietplant(Block block) {
        return block.getType() == Material.NETHER_WART;
    }

    @EventHandler
    public void onOogsten(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (!isWietplant(block)) return;

        Ageable ageable = (Ageable) block.getBlockData();
        boolean volgroeid = ageable.getAge() == ageable.getMaximumAge();
        if (!volgroeid) return;

        // Vervang de standaard drop (nether wart) door onze eigen Wietblaadjes-item.
        event.setDropItems(false);
        int aantal = 1 + (int) (Math.random() * 3); // 1-3 blaadjes per oogst
        block.getWorld().dropItemNaturally(block.getLocation(), plugin.getWietManager().maakBlaadjesItem(aantal));
        event.getPlayer().sendMessage(ChatColor.GREEN + "Je hebt " + aantal + "x Wietblaadjes geoogst!");
    }

    @EventHandler
    public void onVerwerken(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null || block.getType() != Material.CRAFTING_TABLE) return;

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (!plugin.getWietManager().isBlaadjes(hand)) return;

        event.setCancelled(true);
        plugin.getWietManager().startVerwerken(player);
    }
}
