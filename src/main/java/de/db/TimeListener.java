package de.db;

import org.bukkit.World;

// Unsere Klasse ist ein "Runnable", eine Aufgabe, die wiederholt ausgeführt werden kann.
public class TimeListener implements Runnable {

    private final NightSkipManager nightSkipManager;
    private final World world;

    // Wir geben dem Listener den Manager und die Welt mit, die er beobachten soll.
    public TimeListener(NightSkipManager nightSkipManager, World world) {
        this.nightSkipManager = nightSkipManager;
        this.world = world;
    }

    @Override
    public void run() {
        // Diese Methode wird immer wieder vom Server-Scheduler aufgerufen.

        // Wir holen uns die aktuelle In-Game-Zeit in "Ticks".
        // 0 = Sonnenaufgang
        // 6000 = Mittag
        // 13000 = Sonnenuntergang / Nacht beginnt
        // 18000 = Mitternacht
        long currentTime = world.getTime();

        // --- NACHT-ERKENNUNG ---
        // Ist es Nacht (nach 13000 Ticks) UND denkt der Manager noch, es sei Tag?
        if (currentTime >= 13000 && !nightSkipManager.isNight()) {
            // Dann ist genau JETZT der Moment, den Nacht-Zyklus zu starten.
            // Der Manager berechnet die Stimmen und sendet die Chat-Nachricht.
            nightSkipManager.startNightCycle();
        }

        // --- TAG-ERKENNUNG ---
        // Ist es Tag (vor 13000 Ticks) UND denkt der Manager noch, es sei Nacht?
        else if (currentTime < 13000 && nightSkipManager.isNight()) {
            // Dann ist die Nacht vorbei (entweder durch Abstimmung oder normal).
            // Wir setzen das System zurück, damit es für die nächste Nacht bereit ist.
            nightSkipManager.reset();
        }
    }
}