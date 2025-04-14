package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {
    private static final TeleportManager INSTANCE = new TeleportManager();

    private final Map<UUID, Location> backLocations = new HashMap<>();
    private final Map<UUID, TeleportRequest> requests = new HashMap<>();

    private TeleportManager() {
    }

    public static TeleportManager getInstance() {
        return INSTANCE;
    }

    public void sendRequest(Player from, Player to, boolean bring) {
        TeleportRequest request = new TeleportRequest(from.getUniqueId(), to.getUniqueId(), bring);
        requests.put(to.getUniqueId(), request);
    }

    public void acceptRequest(Player target) {
        TeleportRequest request = requests.get(target.getUniqueId());
        if (request == null) return;

        teleport(request.from(), request.to(), request.bring());
        requests.remove(target.getUniqueId());
    }

    public void cancelRequest(Player sender) {
        UUID senderId = sender.getUniqueId();
        requests.values().removeIf(req -> req.from().equals(senderId));
    }

    public void clearRequest(UUID targetId) {
        requests.remove(targetId);
    }

    public void teleport(UUID from, UUID to, boolean bring) {
        // bring (to -> from ) | !bring (from <- to)
        // Should be scheduled for the time interval.
    }

    public void saveBackLocation(Player player) {
        backLocations.put(player.getUniqueId(), player.getLocation());
    }

    public Location getBackLocation(Player player) {
        return backLocations.get(player.getUniqueId());
    }
}

