package com.minetopia.plugin.commands;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KijksaldoCommand implements CommandExecutor {

    private final MinetopiaPlugin plugin;

    public KijksaldoCommand(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) && args.length == 0) {
            sender.sendMessage("Gebruik: /kijksaldo <speler>");
            return true;
        }

        if (args.length == 0) {
            Player player = (Player) sender;
            double saldo = plugin.getEconomyManager().getBalance(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Jouw saldo: " + ChatColor.WHITE + plugin.getEconomyManager().format(saldo));
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target == null || (!target.hasPlayedBefore() && !target.isOnline())) {
            sender.sendMessage(ChatColor.RED + "Die speler is onbekend.");
            return true;
        }
        double saldo = plugin.getEconomyManager().getBalance(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Saldo van " + target.getName() + ": "
                + ChatColor.WHITE + plugin.getEconomyManager().format(saldo));
        return true;
    }
}
