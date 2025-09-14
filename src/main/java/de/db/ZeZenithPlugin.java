package de.db;

import org.bukkit.World;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class ZeZenithPlugin extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");

    // Hier deklarieren wir unsere Manager.
    private ConfigManager configManager;
    private AFKManager afkManager;
    private NightSkipManager nightSkipManager;

    @Override
    public void onEnable() {
        // --- 1. Manager in der richtigen Reihenfolge initialisieren ---
        // Zuerst die Config, da andere davon abhängen könnten.
        this.configManager = new ConfigManager(this);
        this.afkManager = new AFKManager(this);
        this.nightSkipManager = new NightSkipManager(this);
        log.info("[ZeZenithPlugin] All managers have been initialized.");

        // --- 2. Befehle registrieren ---
        getCommand("gn").setExecutor(new GoodNightCommand(this));
        getCommand("zenith").setExecutor(new ZenithAdminCommand(this));
        log.info("[ZeZenithPlugin] Commands have been registered.");

        // --- 3. Event Listeners registrieren ---
        // Der AFKManager lauscht auf Spieler-Aktionen.
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.PLAYER_JOIN, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_QUIT, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_MOVE, this.afkManager, Priority.Normal, this);
        pm.registerEvent(Type.PLAYER_CHAT, this.afkManager, Priority.Normal, this);
        log.info("[ZeZenithPlugin] Event listeners have been registered.");

        // --- 4. Wiederholte Aufgaben (Tasks) starten ---
        startRepeatingTasks();

        log.info("[ZeZenithPlugin] has been successfully enabled! Ready to skip the night.");
    }

    @Override
    public void onDisable() {
        // Stoppe alle Tasks, die von diesem Plugin gestartet wurden. Sehr wichtig!
        getServer().getScheduler().cancelTasks(this);
        log.info("[ZeZenithPlugin] All tasks have been cancelled.");
        log.info("[ZeZenithPlugin] has been disabled.");
    }

    private void startRepeatingTasks() {
        // Finde die Hauptwelt für den TimeListener
        World mainWorld = getServer().getWorlds().get(0);
        if (mainWorld == null) {
            log.severe("[ZeZenithPlugin] COULD NOT FIND A WORLD! The time listener will not work.");
            return;
        }

        // Starte den TimeListener (prüft jede Sekunde die Zeit)
        Runnable timeListener = new TimeListener(this.nightSkipManager, mainWorld);
        getServer().getScheduler().scheduleSyncRepeatingTask(this, timeListener, 100L, 20L);

        // Starte den AFK-Checker (prüft alle 5 Sekunden den AFK-Status)
        Runnable afkChecker = this.afkManager;
        getServer().getScheduler().scheduleSyncRepeatingTask(this, afkChecker, 100L, 100L); // 100 Ticks = 5 Sekunden

        log.info("[ZeZenithPlugin] Repeating tasks (Time & AFK) have been scheduled.");
    }

    // --- Getter für alle Manager ---
    public ConfigManager getConfigManager() { return configManager; }
    public AFKManager getAfkManager() { return afkManager; }
    public NightSkipManager getNightSkipManager() { return nightSkipManager; }
}