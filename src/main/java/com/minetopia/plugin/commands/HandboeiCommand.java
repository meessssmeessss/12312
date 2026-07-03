package com.minetopia.plugin.commands;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HandboeiCommand implements CommandExecutor {

    private final MinetopiaPlugin plugin;

    public HandboeiCommand(MinetopiaPlugin plugin) {
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
            player.sendMessage(ChatColor.RED + "Gebruik: /handboei <speler>");
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

        boolean nuGeboeid = plugin.getJailManager().toggle(target.getUniqueId());
        if (nuGeboeid) {
            player.sendMessage(ChatColor.GREEN + "Je hebt " + target.getName() + " geboeid.");
            target.sendMessage(ChatColor.RED + "Je bent geboeid door " + player.getName() + " en kunt je niet meer bewegen.");
        } else {
            player.sendMessage(ChatColor.GREEN + "Je hebt " + target.getName() + " losgemaakt.");
            target.sendMessage(ChatColor.GREEN + "Je bent losgemaakt door " + player.getName() + ".");
        }
        return true;
    }
}
