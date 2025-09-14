package de.db;

import org.bukkit.util.config.Configuration;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Manages all interactions with the config.yml file.
 * This class handles loading, saving, and providing access to all plugin settings.
 * It is designed to work with the legacy Bukkit configuration API.
 */
public class ConfigManager {

    private final ZeZenithPlugin plugin;
    private final Random random = new Random();
    private final Configuration config;

    // --- Cached Configuration Values ---
    private List<String> goodMorningMessagesList;
    private String customGoodMorningMessage;
    private boolean pluginEnabled;
    private double votePercentage;
    private long afkThresholdMillis;
    private boolean afkMessagesEnabled;
    public String msgNightCanBeSkipped;
    public String msgVoteBroadcast;
    public String msgAlreadyVoted;
    public String msgNotNight;
    public String msgNoPermission;
    public String msgPlayerNowAFK;
    public String msgPlayerNoLongerAFK;

    /**
     * Constructor for the ConfigManager.
     * Ensures the plugin's data folder and config.yml exist, then loads all values.
     * @param plugin The main plugin instance.
     */
    public ConfigManager(ZeZenithPlugin plugin) {
        this.plugin = plugin;
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        this.config = new Configuration(configFile);

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }
        loadConfig();
    }

    /**
     * Loads the config.yml file from disk, sets defaults if necessary,
     * and caches the values in class fields for fast access.
     */
    private void loadConfig() {
        config.load();
        setDefaults();
        config.save();

        // Load values from config into memory
        this.goodMorningMessagesList = config.getStringList("messages.good_morning_list", new ArrayList<>());
        this.customGoodMorningMessage = config.getString("messages.custom_good_morning", "");
        this.pluginEnabled = config.getBoolean("plugin.enabled", true);
        this.afkMessagesEnabled = config.getBoolean("afk.broadcast_messages", true);
        this.votePercentage = config.getDouble("voting.percentage", 0.60);
        this.afkThresholdMillis = (long) config.getInt("afk.minutes", 5) * 60 * 1000;
        this.msgNightCanBeSkipped = config.getString("messages.night_can_be_skipped");
        this.msgVoteBroadcast = config.getString("messages.vote_broadcast");
        this.msgAlreadyVoted = config.getString("messages.already_voted");
        this.msgNotNight = config.getString("messages.not_night");
        this.msgNoPermission = config.getString("messages.no_permission");
        this.msgPlayerNowAFK = config.getString("messages.player_now_afk");
        this.msgPlayerNoLongerAFK = config.getString("messages.player_no_longer_afk");
    }

    /**
     * Populates the config object with default values if they do not exist yet.
     * This prevents errors and creates a default config.yml on first launch.
     */
    private void setDefaults() {
        if (config.getProperty("messages.good_morning_list") == null) {
            config.setProperty("messages.good_morning_list", getDefaultGoodMorningMessages());
        }
        if (config.getProperty("messages.custom_good_morning") == null) {
            config.setProperty("messages.custom_good_morning", "");
        }
        if (config.getProperty("plugin.enabled") == null) config.setProperty("plugin.enabled", true);
        if (config.getProperty("afk.broadcast_messages") == null) config.setProperty("afk.broadcast_messages", true);
        if (config.getProperty("voting.percentage") == null) config.setProperty("voting.percentage", 0.60);
        if (config.getProperty("afk.minutes") == null) config.setProperty("afk.minutes", 5);
        if (config.getProperty("messages.night_can_be_skipped") == null) config.setProperty("messages.night_can_be_skipped", "&eThe sun has set. You can skip the night with /gn.");
        if (config.getProperty("messages.vote_broadcast") == null) config.setProperty("messages.vote_broadcast", "&6{player} says good night ({current}/{required})");
        if (config.getProperty("messages.already_voted") == null) config.setProperty("messages.already_voted", "&cYou have already voted to skip the night.");
        if (config.getProperty("messages.not_night") == null) config.setProperty("messages.not_night", "&cYou can only use this command at night!");
        if (config.getProperty("messages.no_permission") == null) config.setProperty("messages.no_permission", "&cYou do not have permission to use this command.");
        if (config.getProperty("messages.player_now_afk") == null) config.setProperty("messages.player_now_afk", "&7{player} is now AFK.");
        if (config.getProperty("messages.player_no_longer_afk") == null) config.setProperty("messages.player_no_longer_afk", "&7{player} is no longer AFK.");
    }

    // --- Getters ---

    /**
     * Gets the appropriate good morning message.
     * Returns the custom override message if it is set, otherwise returns a random
     * message from the default list.
     * @return The final good morning message to be broadcast.
     */
    public String getFinalGoodMorningMessage() {
        if (customGoodMorningMessage != null && !customGoodMorningMessage.isEmpty()) {
            return customGoodMorningMessage;
        }
        if (goodMorningMessagesList == null || goodMorningMessagesList.isEmpty()) {
            return "Good Morning!"; // Fallback
        }
        return goodMorningMessagesList.get(random.nextInt(goodMorningMessagesList.size()));
    }

    public boolean isPluginEnabled() { return pluginEnabled; }
    public boolean areAfkMessagesEnabled() { return afkMessagesEnabled; }
    public double getVotePercentage() { return votePercentage; }
    public long getAfkThresholdMillis() { return afkThresholdMillis; }

    // --- Setters ---

    /**
     * Updates the custom good morning message and saves it to the config.
     * An empty string disables the override.
     * @param message The new custom message.
     */
    public void setCustomGoodMorningMessage(String message) {
        this.customGoodMorningMessage = message;
        config.setProperty("messages.custom_good_morning", message);
        config.save();
    }

    public void setPluginEnabled(boolean enabled) {
        this.pluginEnabled = enabled;
        config.setProperty("plugin.enabled", enabled);
        config.save();
    }

    public void setAfkMessagesEnabled(boolean enabled) {
        this.afkMessagesEnabled = enabled;
        config.setProperty("afk.broadcast_messages", enabled);
        config.save();
    }

    public void setAfkTime(int minutes) {
        this.afkThresholdMillis = (long) minutes * 60 * 1000;
        config.setProperty("afk.minutes", minutes);
        config.save();
    }

    public void setVotePercentage(double percentage) {
        this.votePercentage = percentage;
        config.setProperty("voting.percentage", percentage);
        config.save();
    }

    /**
     * Updates a specific system message and saves it to the config.
     * @param key The ID of the message to change.
     * @param value The new text for the message.
     */
    public void setMessage(String key, String value) {
        config.setProperty("messages." + key.toLowerCase(), value);
        config.save();
        plugin.onReload(); // Reload managers to apply the new message immediately
    }

    /**
     * Provides the default list of good morning messages.
     * @return A list of default messages.
     */
    private List<String> getDefaultGoodMorningMessages() {
        return new ArrayList<>(Arrays.asList("Rise and shine! The nightmare is over.", "The sun has been summoned. You're welcome.", "Good morning, campers! Time to face the creepers again.", "Let there be light! And there was light.", "Another night skipped, another day of glorious mining awaits.", "The darkness has been democratically defeated.", "Ugh, morning already? Fine.", "Congratulations, you've collectively pressed the fast-forward button.", "The vote was successful. The sun is now legally obligated to rise.", "Night's over. Get back to work.", "The monsters have gone to bed. Now it's your turn to play.", "Did someone order a sunrise? Delivery's here.", "Morning has broken... probably because you broke it.", "The council has spoken. The night is banished.", "Y'all couldn't wait 10 more minutes, could you?", "Daylight has been successfully installed.", "Rise and shine, you magnificent miners.", "The darkness recedes. For now.", "And so, the sun reluctantly returns.", "Good morning. Try not to fall in lava today.", "The power of friendship (and voting) has brought back the sun!", "The night has been cancelled due to popular demand.", "Wakey wakey, eggs and bakey!", "The sun is back. Please try to be productive.", "Your daily dose of Vitamin D is now available.", "Let the blocky adventures continue in glorious daylight!", "The zombies are disappointed, but the day is yours.", "Another beautiful sunrise, sponsored by impatience.", "The darkness has been patched.", "The server has been successfully rebooted into daytime mode.", "Good morning. Please don't punch the trees too hard.", "The majority rules. The night is out.", "Let's get this bread! And diamonds. And iron.", "The nocturnal meeting has been adjourned.", "The sun says hello.", "Alright, you win. It's morning.", "A new day has dawned. What will you build?", "The server's coffee is ready. It's daytime.", "The skeletons have clocked out. Your shift begins.", "The darkness couldn't handle your collective will.", "Welcome to the day shift.", "The world is bright again. Don't forget your sunglasses.", "By your command, the day has returned.", "The night was just a bad dream.", "Let's do the time warp again! It's morning.", "The sun is up, the sky is blue, it's beautiful, and so are you.", "The night has been officially yeeted.", "Morning has been restored from a previous save.", "The dark ages are over. Welcome to the renaissance.", "Okay, okay, I'm up! Sheesh. - The Sun"));
    }
}