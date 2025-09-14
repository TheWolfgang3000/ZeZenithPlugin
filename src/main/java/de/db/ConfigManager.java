package de.db;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ConfigManager {

    private final ZeZenithPlugin plugin;
    private final Random random = new Random();

    // Hier speichern wir die geladenen Einstellungen im Arbeitsspeicher
    private boolean pluginEnabled;
    private double votePercentage;
    private long afkThresholdMillis;
    private List<String> goodMorningMessages;

    // Einzelne Nachrichten
    public String msgNightCanBeSkipped;
    public String msgVoteBroadcast;
    public String msgAlreadyVoted;
    public String msgNotNight;
    public String msgNoPermission;
    public String msgPlayerNowAFK;
    public String msgPlayerNoLongerAFK;


    public ConfigManager(ZeZenithPlugin plugin) {
        this.plugin = plugin;
        loadConfig();
    }

    private void loadConfig() {
        FileConfiguration config = plugin.getConfig();

        // --- Standardwerte definieren ---
        config.addDefault("plugin.enabled", true);
        config.addDefault("voting.percentage", 0.60); // 60%
        config.addDefault("afk.minutes", 5);

        // Nachrichten
        config.addDefault("messages.night_can_be_skipped", "&eThe sun has set. You can skip the night with /gn.");
        config.addDefault("messages.vote_broadcast", "&6{player} says good night ({current}/{required})");
        config.addDefault("messages.already_voted", "&cYou have already voted to skip the night.");
        config.addDefault("messages.not_night", "&cYou can only use this command at night!");
        config.addDefault("messages.no_permission", "&cYou do not have permission to use this command.");
        config.addDefault("messages.player_now_afk", "&7{player} is now AFK.");
        config.addDefault("messages.player_no_longer_afk", "&7{player} is no longer AFK.");

        // Füge die große Liste der "Guten Morgen"-Nachrichten hinzu (nur wenn sie nicht existiert)
        config.addDefault("messages.good_morning_list", getDefaultGoodMorningMessages());

        // Kopiere die Standardwerte in die Konfiguration, falls sie fehlen
        config.options().copyDefaults(true);
        // Speichere die Datei auf der Festplatte (erstellt sie beim ersten Mal)
        plugin.saveConfig();

        // --- Geladene Werte in unsere Variablen einlesen ---
        this.pluginEnabled = config.getBoolean("plugin.enabled");
        this.votePercentage = config.getDouble("voting.percentage");
        this.afkThresholdMillis = config.getLong("afk.minutes") * 60 * 1000;
        this.goodMorningMessages = config.getStringList("messages.good_morning_list");

        // Einzelne Nachrichten laden
        this.msgNightCanBeSkipped = config.getString("messages.night_can_be_skipped");
        this.msgVoteBroadcast = config.getString("messages.vote_broadcast");
        this.msgAlreadyVoted = config.getString("messages.already_voted");
        this.msgNotNight = config.getString("messages.not_night");
        this.msgNoPermission = config.getString("messages.no_permission");
        this.msgPlayerNowAFK = config.getString("messages.player_now_afk");
        this.msgPlayerNoLongerAFK = config.getString("messages.player_no_longer_afk");
    }

    // --- Öffentliche "Getter"-Methoden (Lesezugriff) ---

    public boolean isPluginEnabled() { return pluginEnabled; }
    public double getVotePercentage() { return votePercentage; }
    public long getAfkThresholdMillis() { return afkThresholdMillis; }
    public String getRandomGoodMorningMessage() {
        if (goodMorningMessages == null || goodMorningMessages.isEmpty()) {
            return "Good Morning!"; // Fallback
        }
        return goodMorningMessages.get(random.nextInt(goodMorningMessages.size()));
    }

    // --- Öffentliche "Setter"-Methoden (Schreibzugriff & Speichern) ---

    public void setPluginEnabled(boolean enabled) {
        this.pluginEnabled = enabled;
        plugin.getConfig().set("plugin.enabled", enabled);
        plugin.saveConfig();
    }

    // Hier könnten wir weitere Setter für andere Einstellungen hinzufügen, z.B.
    // public void setVotePercentage(double percentage) { ... }
    // public void addGoodMorningMessage(String message) { ... }

    private List<String> getDefaultGoodMorningMessages() {
        // Hier kommt unsere Liste mit den 50 Nachrichten rein
        return new ArrayList<>(Arrays.asList(
                "Rise and shine! The nightmare is over.",
                "The sun has been summoned. You're welcome.",
                "Good morning, campers! Time to face the creepers again.",
                // ... füge hier alle 50 Nachrichten ein
                "Okay, okay, I'm up! Sheesh. - The Sun"
        ));
    }
}