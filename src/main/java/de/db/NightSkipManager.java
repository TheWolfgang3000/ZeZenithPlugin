package de.db;

import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

public class NightSkipManager {

    private final ZeZenithPlugin plugin;
    private boolean isNight = false;
    private final Set<Player> votedPlayers = new HashSet<>();
    private int requiredVotes = 0;

    public NightSkipManager(ZeZenithPlugin plugin) {
        this.plugin = plugin;
    }

    public void startNightCycle() {
        if (isNight || !plugin.getConfigManager().isPluginEnabled()) return;

        this.isNight = true;
        int activePlayers = plugin.getAfkManager().getActivePlayerCount();

        if (activePlayers <= 2 && activePlayers > 0) {
            this.requiredVotes = 1;
        } else {
            this.requiredVotes = (int) Math.ceil(activePlayers * plugin.getConfigManager().getVotePercentage());
        }

        if (activePlayers > 0) {
            String message = plugin.getConfigManager().msgNightCanBeSkipped;
            plugin.getServer().broadcastMessage(message.replaceAll("&", "§"));
        }
    }

    public void addVote(Player player) {
        if (votedPlayers.contains(player)) {
            String message = plugin.getConfigManager().msgAlreadyVoted;
            player.sendMessage(message.replaceAll("&", "§"));
            return;
        }

        votedPlayers.add(player);
        int currentVotes = votedPlayers.size();

        String message = plugin.getConfigManager().msgVoteBroadcast
                .replace("{player}", player.getName())
                .replace("{current}", String.valueOf(currentVotes))
                .replace("{required}", String.valueOf(requiredVotes));
        plugin.getServer().broadcastMessage(message.replaceAll("&", "§"));

        if (currentVotes >= requiredVotes) {
            skipNight();
        }
    }

    private void skipNight() {
        String message = plugin.getConfigManager().getFinalGoodMorningMessage();
        plugin.getServer().broadcastMessage("§a" + message);

        World world = plugin.getServer().getWorlds().get(0);
        if (world != null) {
            world.setTime(0L);
        }
        reset();
    }

    public void reset() {
        this.isNight = false;
        this.votedPlayers.clear();
        this.requiredVotes = 0;
    }

    public boolean isNight() {
        return isNight;
    }
}