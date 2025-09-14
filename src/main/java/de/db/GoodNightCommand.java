package de.db;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GoodNightCommand implements CommandExecutor {

    // Eine Referenz auf unseren Manager, um ihm Befehle zu erteilen
    private final NightSkipManager nightSkipManager;

    // Konstruktor: Wir übergeben den NightSkipManager von der Hauptklasse
    public GoodNightCommand(NightSkipManager nightSkipManager) {
        this.nightSkipManager = nightSkipManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Schritt 1: Prüfen, ob der Befehl von einem Spieler ausgeführt wird.
        // Die Konsole kann nicht schlafen.
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        // Schritt 2: Den Manager fragen, ob es überhaupt Nacht ist.
        if (!nightSkipManager.isNight()) {
            player.sendMessage(ChatColor.RED + "You can only use this command at night!");
            return true;
        }

        // Schritt 3: Wenn alle Prüfungen bestanden sind, die Stimme an den Manager weiterleiten.
        // Die eigentliche Logik (Spieler zur Liste hinzufügen, zählen, etc.) passiert im Manager.
        nightSkipManager.addVote(player);

        return true;
    }
}