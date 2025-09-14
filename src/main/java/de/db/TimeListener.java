package de.db;

import org.bukkit.World;

/**
 * A repeating task that polls the world time to detect day/night transitions,
 * triggering the night skipping system accordingly.
 */
public class TimeListener implements Runnable {

    private final NightSkipManager nightSkipManager;
    private final World world;

    public TimeListener(NightSkipManager nightSkipManager, World world) {
        this.nightSkipManager = nightSkipManager;
        this.world = world;
    }

    /**
     * This method is executed periodically by the Bukkit scheduler.
     * It checks the current world time and activates or resets the NightSkipManager.
     */
    @Override
    public void run() {
        // Minecraft time is measured in ticks. Night starts around 13000.
        long currentTime = world.getTime();

        // If it is night and the system is not yet active, start it.
        if (currentTime >= 13000 && !nightSkipManager.isNight()) {
            nightSkipManager.startNightCycle();
        }
        // If it is day and the system is still active, reset it.
        else if (currentTime < 13000 && nightSkipManager.isNight()) {
            nightSkipManager.reset();
        }
    }
}