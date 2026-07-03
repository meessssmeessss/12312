package com.minetopia.plugin.commands;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZetsaldoCommand implements CommandExecutor {

    private final MinetopiaPlugin plugin;

    public ZetsaldoCommand(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("minetopia.admin")) {
            sender.sendMessage(ChatColor.RED + "Je hebt geen toestemming voor dit commando.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Gebruik: /zetsaldo <speler> <bedrag>");
            return true;
        }
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        double bedrag;
        try {
            bedrag = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Ongeldig bedrag.");
            return true;
        }
        plugin.getEconomyManager().setBalance(target.getUniqueId(), bedrag);
        sender.sendMessage(ChatColor.GREEN + "Saldo van " + target.getName() + " ingesteld op "
                + plugin.getEconomyManager().format(bedrag) + ".");
        return true;
    }
}
