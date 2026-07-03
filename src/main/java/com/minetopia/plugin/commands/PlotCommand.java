package com.minetopia.plugin.commands;

import com.minetopia.plugin.MinetopiaPlugin;
import com.minetopia.plugin.models.Plot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PlotCommand implements CommandExecutor {

    private final MinetopiaPlugin plugin;
    private static final double UPGRADE_KOSTEN_PER_LEVEL = 1000.0;

    public PlotCommand(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Alleen spelers kunnen dit commando gebruiken.");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0) {
            stuurHulp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "pos1" -> {
                plugin.getPlotManager().setPos1(player.getUniqueId(), player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Positie 1 ingesteld op jouw huidige locatie.");
            }
            case "pos2" -> {
                plugin.getPlotManager().setPos2(player.getUniqueId(), player.getLocation());
                player.sendMessage(ChatColor.GREEN + "Positie 2 ingesteld op jouw huidige locatie.");
            }
            case "create" -> {
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Gebruik: /plot create <naam>");
                    return true;
                }
                Location pos1 = plugin.getPlotManager().getPos1(player.getUniqueId());
                Location pos2 = plugin.getPlotManager().getPos2(player.getUniqueId());
                if (pos1 == null || pos2 == null) {
                    player.sendMessage(ChatColor.RED + "Stel eerst /plot pos1 en /plot pos2 in.");
                    return true;
                }
                String id = args[1].toLowerCase();
                if (plugin.getPlotManager().getPlot(id) != null) {
                    player.sendMessage(ChatColor.RED + "Er bestaat al een plot met die naam.");
                    return true;
                }
                plugin.getPlotManager().createPlot(id, player.getUniqueId(), pos1, pos2);
                player.sendMessage(ChatColor.GREEN + "Plot '" + id + "' aangemaakt!");
            }
            case "delete" -> {
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Gebruik: /plot delete <naam>");
                    return true;
                }
                Plot plot = plugin.getPlotManager().getPlot(args[1].toLowerCase());
                if (!magBeheren(player, plot)) return true;
                plugin.getPlotManager().deletePlot(plot.getId());
                player.sendMessage(ChatColor.GREEN + "Plot verwijderd.");
            }
            case "add" -> {
                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Gebruik: /plot add <naam> <speler>");
                    return true;
                }
                Plot plot = plugin.getPlotManager().getPlot(args[1].toLowerCase());
                if (!magBeheren(player, plot)) return true;
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                plot.addMember(target.getUniqueId());
                player.sendMessage(ChatColor.GREEN + args[2] + " is toegevoegd aan plot '" + plot.getId() + "'.");
            }
            case "remove" -> {
                if (args.length != 3) {
                    player.sendMessage(ChatColor.RED + "Gebruik: /plot remove <naam> <speler>");
                    return true;
                }
                Plot plot = plugin.getPlotManager().getPlot(args[1].toLowerCase());
                if (!magBeheren(player, plot)) return true;
                OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
                plot.removeMember(target.getUniqueId());
                player.sendMessage(ChatColor.GREEN + args[2] + " is verwijderd van plot '" + plot.getId() + "'.");
            }
            case "info" -> {
                Plot plot = plugin.getPlotManager().getPlotAt(player.getLocation());
                if (plot == null) {
                    player.sendMessage(ChatColor.RED + "Je staat niet op een plot.");
                    return true;
                }
                OfflinePlayer owner = Bukkit.getOfflinePlayer(plot.getOwner());
                player.sendMessage(ChatColor.GOLD + "=== Plot: " + plot.getId() + " ===");
                player.sendMessage(ChatColor.GRAY + "Eigenaar: " + ChatColor.WHITE + owner.getName());
                player.sendMessage(ChatColor.GRAY + "Level: " + ChatColor.WHITE + plot.getLevel());
                player.sendMessage(ChatColor.GRAY + "Leden: " + ChatColor.WHITE + plot.getMembers().size());
            }
            case "upgrade" -> {
                if (args.length != 2) {
                    player.sendMessage(ChatColor.RED + "Gebruik: /plot upgrade <naam>");
                    return true;
                }
                Plot plot = plugin.getPlotManager().getPlot(args[1].toLowerCase());
                if (!magBeheren(player, plot)) return true;
                double kosten = plot.getLevel() * UPGRADE_KOSTEN_PER_LEVEL;
                if (!plugin.getEconomyManager().withdraw(player.getUniqueId(), kosten)) {
                    player.sendMessage(ChatColor.RED + "Je hebt niet genoeg geld. Upgrade kost "
                            + plugin.getEconomyManager().format(kosten) + ".");
                    return true;
                }
                plot.setLevel(plot.getLevel() + 1);
                plugin.getLevelManager().addLevel(player.getUniqueId(), 1);
                player.sendMessage(ChatColor.GREEN + "Plot geüpgraded naar level " + plot.getLevel()
                        + "! Jouw speler-level is nu " + plugin.getLevelManager().getLevel(player.getUniqueId()) + ".");
            }
            default -> stuurHulp(player);
        }
        return true;
    }

    private boolean magBeheren(Player player, Plot plot) {
        if (plot == null) {
            player.sendMessage(ChatColor.RED + "Die plot bestaat niet.");
            return false;
        }
        if (!plot.getOwner().equals(player.getUniqueId()) && !player.hasPermission("minetopia.admin")) {
            player.sendMessage(ChatColor.RED + "Je bent geen eigenaar van deze plot.");
            return false;
        }
        return true;
    }

    private void stuurHulp(Player player) {
        List<String> lines = List.of(
                ChatColor.GOLD + "=== /plot commando's ===",
                ChatColor.YELLOW + "/plot pos1" + ChatColor.GRAY + " - stel hoekpunt 1 in",
                ChatColor.YELLOW + "/plot pos2" + ChatColor.GRAY + " - stel hoekpunt 2 in",
                ChatColor.YELLOW + "/plot create <naam>" + ChatColor.GRAY + " - maak een plot",
                ChatColor.YELLOW + "/plot add <naam> <speler>" + ChatColor.GRAY + " - voeg lid toe",
                ChatColor.YELLOW + "/plot remove <naam> <speler>" + ChatColor.GRAY + " - verwijder lid",
                ChatColor.YELLOW + "/plot info" + ChatColor.GRAY + " - info over plot waar je staat",
                ChatColor.YELLOW + "/plot upgrade <naam>" + ChatColor.GRAY + " - level plot & jezelf omhoog",
                ChatColor.YELLOW + "/plot delete <naam>" + ChatColor.GRAY + " - verwijder plot"
        );
        lines.forEach(player::sendMessage);
    }
}
