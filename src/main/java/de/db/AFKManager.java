package de.db;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AFKManager extends PlayerListener implements Runnable {

    private final ZeZenithPlugin plugin;
    private final Map<UUID, Long> lastActivityTime = new HashMap<>();
    private final Set<UUID> afkPlayers = new HashSet<>();
    private long afkThresholdMillis;

    public AFKManager(ZeZenithPlugin plugin) {
        this.plugin = plugin;
        // Lade den Wert aus der Konfiguration
        this.afkThresholdMillis = plugin.getConfigManager().getAfkThresholdMillis();
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            long lastActivity = lastActivityTime.getOrDefault(playerId, now);

            if (now - lastActivity > afkThresholdMillis) {
                if (afkPlayers.add(playerId)) { // .add gibt true zurück, wenn das Element neu war
                    String message = plugin.getConfigManager().msgPlayerNowAFK.replace("{player}", player.getName());
                    plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            } else {
                if (afkPlayers.remove(playerId)) { // .remove gibt true zurück, wenn das Element da war
                    String message = plugin.getConfigManager().msgPlayerNoLongerAFK.replace("{player}", player.getName());
                    plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
                }
            }
        }
    }

    private void updateActivity(Player player) {
        lastActivityTime.put(player.getUniqueId(), System.currentTimeMillis());
    }

    // Alle onPlayer... Methoden bleiben exakt gleich wie vorher.

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) { updateActivity(event.getPlayer()); }
    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        lastActivityTime.remove(playerId);
        afkPlayers.remove(playerId);
    }
    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getFrom().distanceSquared(event.getTo()) > 0.01) { updateActivity(event.getPlayer()); }
    }
    @Override
    public void onPlayerChat(PlayerChatEvent event) { updateActivity(event.getPlayer()); }

    public int getActivePlayerCount() {
        return plugin.getServer().getOnlinePlayers().length - afkPlayers.size();
    }
}