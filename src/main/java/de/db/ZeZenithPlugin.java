package de.db;

import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ZeZenithPlugin extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");

    private ConfigManager configManager;
    private AFKManager afkManager;
    private NightSkipManager nightSkipManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.afkManager = new AFKManager(this);
        this.nightSkipManager = new NightSkipManager(this);
        log.info("[ZeZenithPlugin] All managers have been initialized.");

        getCommand("gn").setExecutor(new GoodNightCommand(this));
        getCommand("zenith").setExecutor(new ZenithAdminCommand(this));
        log.info("[ZeZenithPlugin] Commands have been registered.");

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_JOIN, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_QUIT, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_MOVE, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_CHAT, this.afkManager, Priority.Normal, this);
        log.info("[ZeZenithPlugin] Event listeners have been registered.");

        startRepeatingTasks();
        log.info("[ZeZenithPlugin] has been successfully enabled! Ready to skip the night.");
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        log.info("[ZeZenithPlugin] All tasks have been cancelled.");
        log.info("[ZeZenithPlugin] has been disabled.");
    }

    private void startRepeatingTasks() {
        World mainWorld = getServer().getWorlds().get(0);
        if (mainWorld == null) {
            log.severe("[ZeZenithPlugin] COULD NOT FIND A WORLD! The time listener will not work.");
            return;
        }

        Runnable timeListener = new TimeListener(this.nightSkipManager, mainWorld);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, timeListener, 100L, 20L);

        Runnable afkChecker = this.afkManager;
        getServer().getScheduler().scheduleSyncRepeatingTask(this, afkChecker, 100L, 100L);
        log.info("[ZeZenithPlugin] Repeating tasks (Time & AFK) have been scheduled.");
    }

    public void onReload() {
        this.configManager = new ConfigManager(this);
        this.afkManager = new AFKManager(this);
        log.info("[ZeZenithPlugin] Managers have been reloaded.");
    }

    // --- Getter für alle Manager ---
    public ConfigManager getConfigManager() { return configManager; }
    public AFKManager getAfkManager() { return afkManager; }
    public NightSkipManager getNightSkipManager() { return nightSkipManager; }
}