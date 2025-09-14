package de.db;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZenithAdminCommand implements CommandExecutor {

    private final ZeZenithPlugin plugin;
    // Praktische Referenzen auf die Manager
    private final ConfigManager configManager;
    private final AFKManager afkManager;

    public ZenithAdminCommand(ZeZenithPlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.afkManager = plugin.getAfkManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Schritt 1: Hat der Absender überhaupt die Rechte?
        if (!sender.hasPermission("zenith.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        // Schritt 2: Welcher Unterbefehl wurde eingegeben?
        if (args.length == 0) {
            // Kein Unterbefehl -> Hilfe anzeigen
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "help":
                showHelp(sender);
                break;
            case "toggle":
                togglePlugin(sender);
                break;
            case "reload":
                reloadPluginConfig(sender);
                break;
            case "status":
                showStatus(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use /zenith help to see all commands.");
                break;
        }

        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- ZeZenithPlugin Admin Help ---");
        sender.sendMessage(ChatColor.YELLOW + "/zenith help" + ChatColor.WHITE + " - Shows this help message.");
        sender.sendMessage(ChatColor.YELLOW + "/zenith toggle" + ChatColor.WHITE + " - Enables or disables the plugin.");
        sender.sendMessage(ChatColor.YELLOW + "/zenith reload" + ChatColor.WHITE + " - Reloads the configuration from config.yml.");
        sender.sendMessage(ChatColor.YELLOW + "/zenith status" + ChatColor.WHITE + " - Shows the current status of the plugin.");
        // Hier könnten wir später weitere Befehle hinzufügen (z.B. für Nachrichten-Verwaltung)
    }

    private void togglePlugin(CommandSender sender) {
        boolean isCurrentlyEnabled = configManager.isPluginEnabled();
        configManager.setPluginEnabled(!isCurrentlyEnabled); // Den Wert umkehren und speichern
        if (!isCurrentlyEnabled) {
            sender.sendMessage(ChatColor.GREEN + "ZeZenithPlugin has been enabled.");
        } else {
            sender.sendMessage(ChatColor.RED + "ZeZenithPlugin has been disabled.");
        }
    }

    private void reloadPluginConfig(CommandSender sender) {
        // Wir müssen eine Methode im ConfigManager hinzufügen, um dies zu ermöglichen.
        // Fürs Erste simulieren wir es:
        plugin.reloadConfig(); // Lädt die Datei neu
        // TODO: Eine reload-Methode im ConfigManager erstellen, die die Werte neu einliest.
        sender.sendMessage(ChatColor.GREEN + "Configuration for ZeZenithPlugin has been reloaded.");
    }

    private void showStatus(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "--- ZeZenithPlugin Status ---");

        boolean enabled = configManager.isPluginEnabled();
        sender.sendMessage(ChatColor.YELLOW + "Plugin Status: " + (enabled ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled"));

        double percentage = configManager.getVotePercentage() * 100;
        sender.sendMessage(ChatColor.YELLOW + "Vote Percentage: " + ChatColor.AQUA + String.format("%.0f%%", percentage));

        int activePlayers = afkManager.getActivePlayerCount();
        int totalPlayers = plugin.getServer().getOnlinePlayers().length;
        sender.sendMessage(ChatColor.YELLOW + "Active Players: " + ChatColor.AQUA + activePlayers + "/" + totalPlayers);
    }
}