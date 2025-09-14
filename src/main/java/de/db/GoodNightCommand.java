package de.db;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GoodNightCommand implements CommandExecutor {

    private final ZeZenithPlugin plugin;

    public GoodNightCommand(ZeZenithPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        NightSkipManager nightSkipManager = plugin.getNightSkipManager();

        if (!nightSkipManager.isNight()) {
            String message = plugin.getConfigManager().msgNotNight;
            player.sendMessage(message.replaceAll("&", "§"));
            return true;
        }

        nightSkipManager.addVote(player);
        return true;
    }
}