package de.db;

import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles the core logic for the night skipping feature. This class manages
 * the voting process, tracks votes, and triggers the time change.
 */
public class NightSkipManager {

    private final ZeZenithPlugin plugin;

    // State variables
    private boolean isNight = false;
    private final Set<Player> votedPlayers = new HashSet<>();
    private int requiredVotes = 0;

    public NightSkipManager(ZeZenithPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Initiates the night skipping cycle. This method is called when night begins.
     * It calculates the required number of votes and broadcasts an announcement.
     */
    public void startNightCycle() {
        if (isNight || !plugin.getConfigManager().isPluginEnabled()) return;

        this.isNight = true;
        int activePlayers = plugin.getAfkManager().getActivePlayerCount();

        // Calculate required votes based on the configured percentage,
        // with a special case for 1-2 players.
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

    /**
     * Processes a vote from a player.
     * Adds the player to the set of voters, broadcasts the progress,
     * and checks if the required vote count has been met.
     * @param player The player who voted.
     */
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

        // If threshold is met, skip the night.
        if (currentVotes >= requiredVotes) {
            skipNight();
        }
    }

    /**
     * Executes the night skip by setting the world time to morning
     * and broadcasting a success message.
     */
    private void skipNight() {
        String message = plugin.getConfigManager().getFinalGoodMorningMessage();
        plugin.getServer().broadcastMessage("§a" + message);

        World world = plugin.getServer().getWorlds().get(0);
        if (world != null) {
            world.setTime(0L); // 0L is sunrise
        }
        reset();
    }

    /**
     * Resets the voting state. Called after a successful vote or when day breaks naturally.
     * This clears all votes and prepares the system for the next night.
     */
    public void reset() {
        this.isNight = false;
        this.votedPlayers.clear();
        this.requiredVotes = 0;
    }

    /**
     * Checks if the night skipping system is currently active.
     * @return true if it is night, false otherwise.
     */
    public boolean isNight() {
        return isNight;
    }
}