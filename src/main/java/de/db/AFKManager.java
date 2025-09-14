package de.db;

import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manages the AFK status of players.
 * It listens for player activity events and runs a periodic task to check for inactivity.
 */
public class AFKManager extends PlayerListener implements Runnable {

    private final ZeZenithPlugin plugin;
    private final Map<UUID, Long> lastActivityTime = new HashMap<>();
    private final Set<UUID> afkPlayers = new HashSet<>();
    private long afkThresholdMillis;

    public AFKManager(ZeZenithPlugin plugin) {
        this.plugin = plugin;
        this.afkThresholdMillis = plugin.getConfigManager().getAfkThresholdMillis();
    }

    /**
     * This method is executed periodically by the Bukkit scheduler.
     * It iterates through all online players and updates their AFK status based on inactivity.
     */
    @Override
    public void run() {
        boolean broadcastAfk = plugin.getConfigManager().areAfkMessagesEnabled();
        long now = System.currentTimeMillis();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            long lastActivity = lastActivityTime.getOrDefault(playerId, now);

            // Check if player has exceeded the AFK time threshold
            if (now - lastActivity > afkThresholdMillis) {
                // Add returns true if the player was not already in the set
                if (afkPlayers.add(playerId) && broadcastAfk) {
                    String message = plugin.getConfigManager().msgPlayerNowAFK.replace("{player}", player.getName());
                    plugin.getServer().broadcastMessage(message.replaceAll("&", "§"));
                }
            } else {
                // Player is active. Remove returns true if the player was in the set.
                if (afkPlayers.remove(playerId) && broadcastAfk) {
                    String message = plugin.getConfigManager().msgPlayerNoLongerAFK.replace("{player}", player.getName());
                    plugin.getServer().broadcastMessage(message.replaceAll("&", "§"));
                }
            }
        }
    }

    /**
     * Updates a player's last activity timestamp to the current time.
     * @param player The player to update.
     */
    private void updateActivity(Player player) {
        lastActivityTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    // --- Event Handlers to detect player activity ---

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) { updateActivity(event.getPlayer()); }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean up data for players who log out
        UUID playerId = event.getPlayer().getUniqueId();
        lastActivityTime.remove(playerId);
        afkPlayers.remove(playerId);
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        // Only update on significant movement, not just looking around
        if (event.getFrom().distanceSquared(event.getTo()) > 0.01) {
            updateActivity(event.getPlayer());
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) { updateActivity(event.getPlayer()); }

    /**
     * Calculates the number of players who are not AFK.
     * @return The total count of active players.
     */
    public int getActivePlayerCount() {
        return plugin.getServer().getOnlinePlayers().length - afkPlayers.size();
    }
}