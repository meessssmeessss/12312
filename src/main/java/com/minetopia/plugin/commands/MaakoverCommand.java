package com.minetopia.plugin.commands;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MaakoverCommand implements CommandExecutor {

    private final MinetopiaPlugin plugin;

    public MaakoverCommand(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length != 2) {
            player.sendMessage(ChatColor.RED + "Gebruik: /maakover <speler> <bedrag>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(ChatColor.RED + "Die speler is niet online.");
            return true;
        }
        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Je kunt geen geld naar jezelf overmaken.");
            return true;
        }

        double bedrag;
        try {
            bedrag = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Ongeldig bedrag.");
            return true;
        }
        if (bedrag <= 0) {
            player.sendMessage(ChatColor.RED + "Het bedrag moet groter dan 0 zijn.");
            return true;
        }

        boolean success = plugin.getEconomyManager().transfer(player.getUniqueId(), target.getUniqueId(), bedrag);
        if (!success) {
            player.sendMessage(ChatColor.RED + "Je hebt niet genoeg saldo voor deze overboeking.");
            return true;
        }

        String bedragStr = plugin.getEconomyManager().format(bedrag);
        player.sendMessage(ChatColor.GREEN + "Je hebt " + bedragStr + " overgemaakt naar " + target.getName() + ".");
        target.sendMessage(ChatColor.GREEN + "Je hebt " + bedragStr + " ontvangen van " + player.getName() + ".");
        return true;
    }
}
