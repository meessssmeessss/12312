package com.minetopia.plugin.managers;

import com.minetopia.plugin.MinetopiaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Bouwt voor elke online speler een sidebar-scoreboard op en ververst deze
 * elke seconde met de actuele Nederlandse tijd/datum, saldo, level en prefix.
 */
public class ScoreboardManager {

    private final MinetopiaPlugin plugin;
    private static final ZoneId NL_ZONE = ZoneId.of("Europe/Amsterdam");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy", new Locale("nl", "NL"));

    public ScoreboardManager(MinetopiaPlugin plugin) {
        this.plugin = plugin;
    }

    /** Start de herhalende taak die elke seconde (20 ticks) alle scoreboards ververst. */
    public void start() {
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateScoreboard(player);
            }
        }, 20L, 20L);
    }

    public void updateScoreboard(Player player) {
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("minetopia", "dummy", "§6§lMineTopia");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        ZonedDateTime now = ZonedDateTime.now(NL_ZONE);
        String tijd = now.format(TIME_FMT);
        String datum = now.format(DATE_FMT);

        double saldo = plugin.getEconomyManager().getBalance(player.getUniqueId());
        int level = plugin.getLevelManager().getLevel(player.getUniqueId());
        String prefix = plugin.getPrefixManager().getFullPrefix(player);

        // Scores moeten uniek zijn; we gebruiken kleurcodes als "spacers" om duplicaten te voorkomen.
        int score = 10;
        setLine(objective, "§7" + datum, score--);
        setLine(objective, "§f" + tijd, score--);
        setLine(objective, "§e ", score--);
        setLine(objective, "§aSaldo: §f" + plugin.getEconomyManager().format(saldo), score--);
        setLine(objective, "§bLevel: §f" + level, score--);
        setLine(objective, "§d ", score--);
        setLine(objective, "§7Rang: §f" + org.bukkit.ChatColor.stripColor(prefix), score--);

        player.setScoreboard(board);
    }

    private void setLine(Objective objective, String text, int score) {
        Score s = objective.getScore(text);
        s.setScore(score);
    }
}
