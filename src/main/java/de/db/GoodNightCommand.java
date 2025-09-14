package de.db;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Handles the /gn and /goodnight commands for players.
 */
public class GoodNightCommand implements CommandExecutor {

    private final ZeZenithPlugin plugin;

    public GoodNightCommand(ZeZenithPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the command when a player types /gn.
     * @param sender The command sender.
     * @param command The command object.
     * @param label The command alias used.
     * @param args The command arguments.
     * @return true if the command was handled successfully.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the command is run by a player, not the console.
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        NightSkipManager nightSkipManager = plugin.getNightSkipManager();

        // Check if it is currently night.
        if (!nightSkipManager.isNight()) {
            String message = plugin.getConfigManager().msgNotNight;
            player.sendMessage(message.replaceAll("&", "§"));
            return true;
        }

        // Delegate the vote to the manager.
        nightSkipManager.addVote(player);
        return true;
    }
}