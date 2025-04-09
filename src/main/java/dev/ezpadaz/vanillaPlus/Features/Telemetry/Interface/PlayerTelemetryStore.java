package dev.ezpadaz.vanillaPlus.Features.Telemetry.Interface;

import org.bukkit.entity.Player;

public interface PlayerTelemetryStore {
    void savePlayerDeath(Player player);

    void savePlayerChat(Player player, String message);

    void savePlayerLogin(Player player);

    void savePlayerQuit(Player player);
}
