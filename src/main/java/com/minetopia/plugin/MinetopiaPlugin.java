package com.minetopia.plugin;

import com.minetopia.plugin.commands.*;
import com.minetopia.plugin.listeners.*;
import com.minetopia.plugin.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class MinetopiaPlugin extends JavaPlugin {

    private static MinetopiaPlugin instance;

    private EconomyManager economyManager;
    private LevelManager levelManager;
    private PrefixManager prefixManager;
    private PlotManager plotManager;
    private JailManager jailManager;
    private ScoreboardManager scoreboardManager;
    private WietManager wietManager;

    @Override
    public void onEnable() {
        instance = this;

        // Zorg dat de data-map bestaat
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Managers initialiseren (laden bestaande data van schijf)
        this.economyManager = new EconomyManager(this);
        this.levelManager = new LevelManager(this);
        this.prefixManager = new PrefixManager(this);
        this.plotManager = new PlotManager(this);
        this.jailManager = new JailManager();
        this.wietManager = new WietManager(this);
        this.scoreboardManager = new ScoreboardManager(this);

        // Commando's registreren
        getCommand("maakover").setExecutor(new MaakoverCommand(this));
        getCommand("kijksaldo").setExecutor(new KijksaldoCommand(this));
        getCommand("zetsaldo").setExecutor(new ZetsaldoCommand(this));
        getCommand("handboei").setExecutor(new HandboeiCommand(this));
        getCommand("fouilleer").setExecutor(new FouilleerCommand(this));
        getCommand("plot").setExecutor(new PlotCommand(this));
        getCommand("dealer").setExecutor(new DealerCommand(this));

        // Listeners registreren
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlotProtectionListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinLeaveListener(this), this);
        getServer().getPluginManager().registerEvents(new JailListener(this), this);
        getServer().getPluginManager().registerEvents(new WietListener(this), this);
        getServer().getPluginManager().registerEvents(new DealerListener(this), this);

        // Scoreboard elke seconde updaten voor alle online spelers
        scoreboardManager.start();

        getLogger().info("MinetopiaPlugin is ingeschakeld!");
    }

    @Override
    public void onDisable() {
        if (economyManager != null) economyManager.saveAll();
        if (levelManager != null) levelManager.saveAll();
        if (plotManager != null) plotManager.saveAll();
        if (wietManager != null) wietManager.saveAll();
        getLogger().info("MinetopiaPlugin is uitgeschakeld, data opgeslagen.");
    }

    public static MinetopiaPlugin getInstance() {
        return instance;
    }

    public EconomyManager getEconomyManager() { return economyManager; }
    public LevelManager getLevelManager() { return levelManager; }
    public PrefixManager getPrefixManager() { return prefixManager; }
    public PlotManager getPlotManager() { return plotManager; }
    public JailManager getJailManager() { return jailManager; }
    public ScoreboardManager getScoreboardManager() { return scoreboardManager; }
    public WietManager getWietManager() { return wietManager; }
}
