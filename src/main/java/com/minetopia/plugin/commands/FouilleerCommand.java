package com.minetopia.plugin.commands;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FouilleerCommand implements CommandExecutor {

    private final MinetopiaPlugin plugin;

    public FouilleerCommand(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("minetopia.agent")) {
            player.sendMessage(ChatColor.RED + "Je moet Agent zijn om dit te doen.");
            return true;
        }
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Gebruik: /fouilleer <speler>");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Die speler is niet online.");
            return true;
        }
        if (target.getLocation().distance(player.getLocation()) > 5) {
            player.sendMessage(ChatColor.RED + "Je moet dichter bij de speler staan.");
            return true;
        }

        // Alleen-lezen kopie van de inventory tonen aan de agent.
        Inventory view = Bukkit.createInventory(null, 36, "Fouillering: " + target.getName());
        ItemStack[] contents = target.getInventory().getContents();
        for (int i = 0; i < contents.length && i < 36; i++) {
            if (contents[i] != null) {
                view.setItem(i, contents[i].clone());
            }
        }
        player.openInventory(view);
        player.sendMessage(ChatColor.GREEN + "Je doorzoekt de zakken van " + target.getName() + "...");
        target.sendMessage(ChatColor.RED + "Je wordt gefouilleerd door " + player.getName() + ".");
        return true;
    }
}
