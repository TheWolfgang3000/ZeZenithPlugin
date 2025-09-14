package de.db;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

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

    private void togglePlugin(CommandSender sender) {
        ConfigManager configManager = plugin.getConfigManager();
        boolean isCurrentlyEnabled = configManager.isPluginEnabled();
        configManager.setPluginEnabled(!isCurrentlyEnabled);
        sender.sendMessage("§aZeZenithPlugin has been " + (!isCurrentlyEnabled ? "§2enabled" : "§cdisabled") + "§a.");
    }

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
            plugin.getConfigManager().setVotePercentage(percentage / 100.0);
            sender.sendMessage("§aVote percentage has been set to §e" + String.format("%.0f%%", percentage) + "§a.");
        } catch (NumberFormatException e) {
            sender.sendMessage("§c'" + args[1] + "' is not a valid number.");
        }
    }

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

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            messageBuilder.append(args[i]).append(" ");
        }

        String message = messageBuilder.toString().trim();
        plugin.getConfigManager().setMessage(id, message);
        sender.sendMessage("§aMessage for ID '§e" + id + "§a' has been updated.");
    }

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

    private void resetCustomMessage(CommandSender sender) {
        plugin.getConfigManager().setCustomGoodMorningMessage("");
        sender.sendMessage("§aCustom good morning message has been reset. The plugin will now use random messages again.");
    }

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