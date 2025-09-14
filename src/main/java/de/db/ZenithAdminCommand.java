package de.db;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

/**
 * Handles all administrative commands under the /zenith base command.
 * Allows OPs to configure the plugin in-game.
 */
public class ZenithAdminCommand implements CommandExecutor {

    private final ZeZenithPlugin plugin;

    public ZenithAdminCommand(ZeZenithPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check for the required permission.
        if (!sender.hasPermission("zenith.admin")) {
            String message = plugin.getConfigManager().msgNoPermission;
            sender.sendMessage(message.replaceAll("&", "§"));
            return true;
        }

        // If no subcommand is provided, show the help menu.
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // Route to the appropriate method based on the subcommand.
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
            case "afkmessages":
                toggleAfkMessages(sender, args);
                break;
            case "setafktime":
                setAfkTime(sender, args);
                break;
            case "setpercentage":
                setVotePercentage(sender, args);
                break;
            case "setmessage":
                setMessage(sender, args);
                break;
            case "setcustommessage":
                setCustomMessage(sender, args);
                break;
            case "resetcustommessage":
                resetCustomMessage(sender);
                break;
            default:
                sender.sendMessage("§cUnknown subcommand. Use /zenith help to see all commands.");
                break;
        }
        return true;
    }

    /**
     * Displays the help menu with all available admin commands.
     * @param sender The command sender to receive the message.
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage("§6--- ZeZenithPlugin Admin Help ---");
        sender.sendMessage("§e/zenith help§f - Shows this help message.");
        sender.sendMessage("§e/zenith status§f - Shows the current status.");
        sender.sendMessage("§e/zenith toggle§f - Enables or disables the plugin.");
        sender.sendMessage("§e/zenith reload§f - Reloads the configuration.");
        sender.sendMessage("§6--- Settings ---");
        sender.sendMessage("§e/zenith afkmessages <on|off>§f - Toggles AFK broadcast messages.");
        sender.sendMessage("§e/zenith setafktime <minutes>§f - Sets the AFK timer.");
        sender.sendMessage("§e/zenith setpercentage <1-100>§f - Sets the required vote percentage.");
        sender.sendMessage("§6--- Messages ---");
        sender.sendMessage("§e/zenith setmessage <id> <text>§f - Sets a system message.");
        sender.sendMessage("§e/zenith setcustommessage <text>§f - Sets the custom good morning message.");
        sender.sendMessage("§e/zenith resetcustommessage§f - Reverts to random good morning messages.");
    }

    /**
     * Toggles the entire plugin on or off.
     * @param sender The command sender.
     */
    private void togglePlugin(CommandSender sender) {
        ConfigManager configManager = plugin.getConfigManager();
        boolean isCurrentlyEnabled = configManager.isPluginEnabled();
        configManager.setPluginEnabled(!isCurrentlyEnabled);
        sender.sendMessage("§aZeZenithPlugin has been " + (!isCurrentlyEnabled ? "§2enabled" : "§cdisabled") + "§a.");
    }

    /**
     * Toggles the AFK broadcast messages on or off.
     * @param sender The command sender.
     * @param args The command arguments.
     */
    private void toggleAfkMessages(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /zenith afkmessages <on|off>");
            return;
        }
        ConfigManager configManager = plugin.getConfigManager();
        if (args[1].equalsIgnoreCase("on")) {
            configManager.setAfkMessagesEnabled(true);
            sender.sendMessage("§aAFK broadcast messages are now §2enabled§a.");
        } else if (args[1].equalsIgnoreCase("off")) {
            configManager.setAfkMessagesEnabled(false);
            sender.sendMessage("§aAFK broadcast messages are now §cdisabled§a.");
        } else {
            sender.sendMessage("§cUsage: /zenith afkmessages <on|off>");
        }
    }

    /**
     * Sets the number of minutes of inactivity before a player is marked as AFK.
     * @param sender The command sender.
     * @param args The command arguments.
     */
    private void setAfkTime(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /zenith setafktime <minutes>");
            return;
        }
        try {
            int minutes = Integer.parseInt(args[1]);
            if (minutes <= 0) {
                sender.sendMessage("§cThe number must be positive.");
                return;
            }
            plugin.getConfigManager().setAfkTime(minutes);
            sender.sendMessage("§aAFK time has been set to §e" + minutes + "§a minute(s).");
        } catch (NumberFormatException e) {
            sender.sendMessage("§c'" + args[1] + "' is not a valid number.");
        }
    }

    /**
     * Sets the percentage of active players required to skip the night.
     * @param sender The command sender.
     * @param args The command arguments.
     */
    private void setVotePercentage(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /zenith setpercentage <1-100>");
            return;
        }
        try {
            double percentage = Double.parseDouble(args[1]);
            if (percentage < 1 || percentage > 100) {
                sender.sendMessage("§cThe percentage must be between 1 and 100.");
                return;
            }
            plugin.getConfigManager().setVotePercentage(percentage / 100.0); // Convert to decimal for calculation
            sender.sendMessage("§aVote percentage has been set to §e" + String.format("%.0f%%", percentage) + "§a.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§c'" + args[1] + "' is not a valid number.");
        }
    }

    /**
     * Sets the text for a specific system message.
     * @param sender The command sender.
     * @param args The command arguments, including the message ID and new text.
     */
    private void setMessage(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /zenith setmessage <id> <text>");
            sender.sendMessage("§eValid IDs: night_start, vote_cast, already_voted, not_night, afk_on, afk_off");
            return;
        }

        String id = args[1].toLowerCase();
        List<String> validIds = Arrays.asList("night_start", "vote_cast", "already_voted", "not_night", "afk_on", "afk_off");
        if (!validIds.contains(id)) {
            sender.sendMessage("§cInvalid message ID. Use one of the following: " + String.join(", ", validIds));
            return;
        }

        // Reconstruct the message from the remaining arguments
        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }

        String message = messageBuilder.toString().trim();
        plugin.getConfigManager().setMessage(id, message);
        sender.sendMessage("§aMessage for ID '§e" + id + "§a' has been updated.");
    }

    /**
     * Sets the custom good morning message, which overrides the random ones.
     * @param sender The command sender.
     * @param args The command arguments, including the new text.
     */
    private void setCustomMessage(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /zenith setcustommessage <text>");
            return;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }

        String message = messageBuilder.toString().trim();
        plugin.getConfigManager().setCustomGoodMorningMessage(message);
        sender.sendMessage("§aCustom good morning message has been set. It will now override the random messages.");
    }

    /**
     * Resets the custom good morning message, reverting to the default random pool.
     * @param sender The command sender.
     */
    private void resetCustomMessage(CommandSender sender) {
        plugin.getConfigManager().setCustomGoodMorningMessage(""); // An empty string disables the override
        sender.sendMessage("§aCustom good morning message has been reset. The plugin will now use random messages again.");
    }

    /**
     * Displays a detailed status overview of the plugin's current configuration.
     * @param sender The command sender.
     */
    private void showStatus(CommandSender sender) {
        ConfigManager configManager = plugin.getConfigManager();
        AFKManager afkManager = plugin.getAfkManager();

        sender.sendMessage("§6--- ZeZenithPlugin Status ---");

        boolean enabled = configManager.isPluginEnabled();
        sender.sendMessage("§ePlugin Status: " + (enabled ? "§aEnabled" : "§cDisabled"));

        boolean afkMsgs = configManager.areAfkMessagesEnabled();
        sender.sendMessage("§eAFK Broadcast: " + (afkMsgs ? "§aEnabled" : "§cDisabled"));

        double percentage = configManager.getVotePercentage() * 100;
        sender.sendMessage("§eVote Percentage: §b" + String.format("%.0f%%", percentage));

        long afkMinutes = configManager.getAfkThresholdMillis() / 60000;
        sender.sendMessage("§eAFK Time: §b" + afkMinutes + " minute(s)");

        int activePlayers = afkManager.getActivePlayerCount();
        int totalPlayers = plugin.getServer().getOnlinePlayers().length;
        sender.sendMessage("§eActive Players: §b" + activePlayers + "/" + totalPlayers);
    }
}