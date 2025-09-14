package de.db;

import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Main class for the ZeZenithPlugin.
 * Handles the plugin's lifecycle, including startup, shutdown, and the initialization
 * of all managers, commands, and listeners.
 */
public class ZeZenithPlugin extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");

    // Manager instances
    private ConfigManager configManager;
    private AFKManager afkManager;
    private NightSkipManager nightSkipManager;

    /**
     * Called when the plugin is enabled.
     */
    @Override
    public void onEnable() {
        // 1. Initialize all managers in the correct order.
        // ConfigManager must be first as other managers depend on its values.
        this.configManager = new ConfigManager(this);
        this.afkManager = new AFKManager(this);
        this.nightSkipManager = new NightSkipManager(this);
        log.info("[ZeZenithPlugin] All managers have been initialized.");

        // 2. Register command executors to link commands to their handling classes.
        getCommand("gn").setExecutor(new GoodNightCommand(this));
        getCommand("zenith").setExecutor(new ZenithAdminCommand(this));
        log.info("[ZeZenithPlugin] Commands have been registered.");

        // 3. Register event listeners. The AFKManager listens to player activity.
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_JOIN, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_QUIT, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_MOVE, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_CHAT, this.afkManager, Priority.Normal, this);
        log.info("[ZeZenithPlugin] Event listeners have been registered.");

        // 4. Schedule repeating tasks for time and AFK checks.
        startRepeatingTasks();

        log.info("[ZeZenithPlugin] has been successfully enabled! Ready to skip the night.");
    }

    /**
     * Called when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        // Cancel all scheduled tasks to prevent memory leaks and errors on shutdown.
        getServer().getScheduler().cancelTasks(this);
        log.info("[ZeZenithPlugin] All tasks have been cancelled.");
        log.info("[ZeZenithPlugin] has been disabled.");
    }

    /**
     * Initializes and schedules the plugin's repeating tasks.
     */
    private void startRepeatingTasks() {
        World mainWorld = getServer().getWorlds().get(0);
        if (mainWorld == null) {
            log.severe("[ZeZenithPlugin] COULD NOT FIND A WORLD! The time listener will not work.");
            return;
        }

        // Schedule the time listener to check for day/night transitions every second (20 ticks).
        Runnable timeListener = new TimeListener(this.nightSkipManager, mainWorld);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, timeListener, 100L, 20L);

        // Schedule the AFK manager to check player status every 5 seconds (100 ticks).
        Runnable afkChecker = this.afkManager;
        getServer().getScheduler().scheduleSyncRepeatingTask(this, afkChecker, 100L, 100L);

        log.info("[ZeZenithPlugin] Repeating tasks (Time & AFK) have been scheduled.");
    }

    /**
     * Handles the logic for reloading the plugin's configuration.
     * Re-initializes managers that depend on the config file.
     */
    public void onReload() {
        this.configManager = new ConfigManager(this);
        this.afkManager = new AFKManager(this);
        log.info("[ZeZenithPlugin] Managers have been reloaded.");
    }

    // --- Getters for Manager Instances ---

    /** @return The active ConfigManager instance. */
    public ConfigManager getConfigManager() { return configManager; }

    /** @return The active AFKManager instance. */
    public AFKManager getAfkManager() { return afkManager; }

    /** @return The active NightSkipManager instance. */
    public NightSkipManager getNightSkipManager() { return nightSkipManager; }
}