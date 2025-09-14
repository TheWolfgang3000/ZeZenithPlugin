package de.db;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ZenithAdminCommand implements CommandExecutor {

    private final ZeZenithPlugin plugin;

    public ZenithAdminCommand(ZeZenithPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("zenith.admin")) {
            String message = plugin.getConfigManager().msgNoPermission;
            sender.sendMessage(message.replaceAll("&", "§"));
            return true;
        }

        if (args.length == 0) {
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
                plugin.onReload();
                sender.sendMessage("§aConfiguration for ZeZenithPlugin has been reloaded.");
                break;
            case "status":
                showStatus(sender);
                break;
            default:
                sender.sendMessage("§cUnknown subcommand. Use /zenith help to see all commands.");
                break;
        }
        return true;
    }

    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6--- ZeZenithPlugin Admin Help ---");
        sender.sendMessage("§e/zenith help§f - Shows this help message.");
        sender.sendMessage("§e/zenith toggle§f - Enables or disables the plugin.");
        sender.sendMessage("§e/zenith reload§f - Reloads the configuration from config.yml.");
        sender.sendMessage("§e/zenith status§f - Shows the current status of the plugin.");
    }

    private void togglePlugin(CommandSender sender) {
        ConfigManager configManager = plugin.getConfigManager();
        boolean isCurrentlyEnabled = configManager.isPluginEnabled();
        configManager.setPluginEnabled(!isCurrentlyEnabled);
        if (!isCurrentlyEnabled) {
            sender.sendMessage("§aZeZenithPlugin has been enabled.");
        } else {
            sender.sendMessage("§cZeZenithPlugin has been disabled.");
        }
    }

    private void showStatus(CommandSender sender) {
        ConfigManager configManager = plugin.getConfigManager();
        AFKManager afkManager = plugin.getAfkManager();

        sender.sendMessage("§6--- ZeZenithPlugin Status ---");

        boolean enabled = configManager.isPluginEnabled();
        sender.sendMessage("§ePlugin Status: " + (enabled ? "§aEnabled" : "§cDisabled"));

        double percentage = configManager.getVotePercentage() * 100;
        sender.sendMessage("§eVote Percentage: §b" + String.format("%.0f%%", percentage));

        int activePlayers = afkManager.getActivePlayerCount();
        int totalPlayers = plugin.getServer().getOnlinePlayers().length;
        sender.sendMessage("§eActive Players: §b" + activePlayers + "/" + totalPlayers);
    }
}