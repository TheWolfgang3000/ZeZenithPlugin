package de.db;

import org.bukkit.World;

public class TimeListener implements Runnable {

    private final NightSkipManager nightSkipManager;
    private final World world;

    public TimeListener(NightSkipManager nightSkipManager, World world) {
        this.nightSkipManager = nightSkipManager;
        this.world = world;
    }

    @Override
    public void run() {
        long currentTime = world.getTime();

        if (currentTime >= 13000 && !nightSkipManager.isNight()) {
            nightSkipManager.startNightCycle();
        }
        else if (currentTime < 13000 && nightSkipManager.isNight()) {
            nightSkipManager.reset();
        }
    }
}