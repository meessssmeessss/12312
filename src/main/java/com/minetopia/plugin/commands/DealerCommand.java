package com.minetopia.plugin.commands;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Material;

public class DealerCommand implements CommandExecutor {

    private final MinetopiaPlugin plugin;

    public DealerCommand(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("minetopia.admin")) {
            player.sendMessage(ChatColor.RED + "Je hebt geen toestemming voor dit commando.");
            return true;
        }
        if (args.length != 1 || !args[0].equalsIgnoreCase("spawn")) {
            player.sendMessage(ChatColor.RED + "Gebruik: /dealer spawn");
            return true;
        }

        Villager villager = player.getWorld().spawn(player.getLocation(), Villager.class);
        villager.setCustomName(ChatColor.DARK_GRAY + "Man met Bivakmuts");
        villager.setCustomNameVisible(true);
        villager.setAI(true);
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.setProfession(Villager.Profession.NONE);
        villager.setPersistent(true);
        // Zwarte leren helm als eenvoudige "bivakmuts" look
        villager.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));

        player.sendMessage(ChatColor.GREEN + "De wietdealer is gespawned! Rechtsklik hem met Verwerkte Wiet in je hand om te verkopen.");
        return true;
    }
}
