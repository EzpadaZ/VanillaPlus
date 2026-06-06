package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import dev.ezpadaz.vanillaPlus.Utils.EffectHelper;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class TeleportManager {
    private static final TeleportManager INSTANCE = new TeleportManager();

    private final Map<UUID, BackLocation> backLocations = new HashMap<>();
    private final Map<String, TeleportRequest> requests = new HashMap<>();
    private final Map<String, Integer> activeTasks = new HashMap<>();

    private int TELEPORT_THRESHOLD = 0;
    private int TELEPORT_DELAY = 0;

    private TeleportManager() {

    }

    public static TeleportManager getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        TELEPORT_DELAY = GeneralHelper.getConfigInt("features.teleport.delay");
        TELEPORT_THRESHOLD = GeneralHelper.getConfigInt("features.teleport.threshold");
    }

    public void clearQueue() {
        for (String requestUUID : new ArrayList<>(requests.keySet())) {
            cleanupRequest(requestUUID);
        }
    }

    public void sendRequest(Player from, Player to, boolean bring) {
        if (from == null) {
            return;
        }

        if (to == null) {
            MessageHelper.send(from, GeneralHelper.getLangString("features.teleport.tp-request-target-offline").replace("%p", "desconocido"));
            return;
        }

        if (from.getUniqueId().equals(to.getUniqueId())) {
            MessageHelper.send(from, GeneralHelper.getLangString("features.teleport.tp-self"));
            return;
        }

        if (!TeleportUtils.isSafe(from.getLocation()) && bring) {
            MessageHelper.send(from, GeneralHelper.getLangString("features.teleport.tp-unsafe-location"));
            return;
        }

        if (!bring && !TeleportUtils.isSafe(to.getLocation())) {
            MessageHelper.send(from, GeneralHelper.getLangString("features.teleport.tp-request-unsafe-location").replace("%p", to.getName()));
            return;
        }

        UUID requestUUID = GeneralHelper.generateUUID();
        TeleportRequest request = new TeleportRequest(requestUUID.toString(), from.getUniqueId(), to.getUniqueId(), bring, GeneralHelper.toISOString(GeneralHelper.getISODate()));

        requests.put(requestUUID.toString(), request);

        String actionText = bring
                ? GeneralHelper.getLangString("features.teleport.tp-target-action-bring-true").replace("%o", from.getName())
                : GeneralHelper.getLangString("features.teleport.tp-target-action-bring-false").replace("%o", from.getName());

        Component targetMessage = text(actionText + " ", GRAY).append(Component.newline())
                .append(text(GeneralHelper.getLangString("features.teleport.tp-target-action-accept"), GREEN, BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp accept " + requestUUID))
                        .hoverEvent(HoverEvent.showText(text(GeneralHelper.getLangString("features.teleport.tp-target-action-accept-description")))))
                .append(space()).append(space())
                .append(text(GeneralHelper.getLangString("features.teleport.tp-target-action-reject"), RED, BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp cancel " + requestUUID))
                        .hoverEvent(HoverEvent.showText(text(GeneralHelper.getLangString("features.teleport.tp-target-action-reject-description")))));

        to.sendMessage(targetMessage);

        Component originMessage = Component.text(GeneralHelper.getLangString("features.teleport.tp-origin-confirmation") + " ").color(NamedTextColor.GREEN)
                .append(Component.text(GeneralHelper.getLangString("features.teleport.tp-origin-action-cancel")).color(NamedTextColor.RED).decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp cancel " + requestUUID))
                        .hoverEvent(HoverEvent.showText(Component.text(GeneralHelper.getLangString("features.teleport.tp-origin-action-cancel-description")))));

        from.sendMessage(originMessage);

        Integer teleportTaskID = SchedulerHelper.scheduleTask(requestUUID.toString(), () -> {
            cleanupRequest(requestUUID.toString());

            if (to.isOnline()) {
                MessageHelper.send(to, GeneralHelper.getLangString("features.teleport.tp-target-expired").replace("%p", from.getName()));
            }

            if (from.isOnline()) {
                MessageHelper.send(from, GeneralHelper.getLangString("features.teleport.tp-origin-expired").replace("%p", to.getName()));
            }
        }, TELEPORT_THRESHOLD);
        activeTasks.put(requestUUID.toString(), teleportTaskID);
    }

    public void acceptRequest(Player target, String teleportID) {
        acceptTeleportRequest(target, teleportID);
    }

    public void acceptRequest(Player target) {
        String requestID = getLatestTeleportRequest(target);

        if (requestID.isEmpty()) {
            MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-accept-no-request"));
            return;
        }

        acceptTeleportRequest(target, requestID);
    }

    private void acceptTeleportRequest(Player target, String teleportID) {
        TeleportRequest request = requests.get(teleportID);
        if (request == null) {
            MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-accept-no-request"));
            return;
        }

        if (!request.to().equals(target.getUniqueId())) {
            MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-request-not-yours"));
            return;
        }

        Player origin = Bukkit.getPlayer(request.from());
        if (origin == null) {
            cleanupRequest(teleportID);
            MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-request-origin-offline").replace("%p", getPlayerName(request.from())));
            return;
        }

        cancelExpirationTask(teleportID);
        MessageHelper.send(origin, GeneralHelper.getLangString("features.teleport.tp-accept-origin-message"));
        MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-accept-target-message"));

        teleport(teleportID);
    }

    public void cancelRequest(Player sender, String requestUUID) {
        cancelTeleportRequest(sender, requestUUID);
    }

    public void cancelRequest(Player sender) {
        String requestUUID = getLatestTeleportRequest(sender);

        if (requestUUID.isEmpty()) {
            requestUUID = getLatestTeleportRequestFrom(sender);
        }

        if (requestUUID.isEmpty()) {
            return;
        }

        cancelTeleportRequest(sender, requestUUID);
    }

    private void cancelTeleportRequest(Player sender, String requestUUID) {
        TeleportRequest request = requests.get(requestUUID);

        if (request == null) {
            MessageHelper.send(sender, GeneralHelper.getLangString("features.teleport.tp-cancel-no-request"));
            return;
        }

        boolean isOrigin = request.from().equals(sender.getUniqueId());
        boolean isTarget = request.to().equals(sender.getUniqueId());
        if (!isOrigin && !isTarget) {
            MessageHelper.send(sender, GeneralHelper.getLangString("features.teleport.tp-request-not-yours"));
            return;
        }

        Player origin = Bukkit.getPlayer(request.from());
        Player target = Bukkit.getPlayer(request.to());

        cleanupRequest(requestUUID);

        if (isOrigin) {
            MessageHelper.send(sender, GeneralHelper.getLangString("features.teleport.tp-cancel-origin-message"));
            sendIfOnline(target, GeneralHelper.getLangString("features.teleport.tp-cancel-origin-target-message").replace("%p", sender.getName()));
        } else {
            MessageHelper.send(sender, GeneralHelper.getLangString("features.teleport.tp-cancel-target-message"));
            sendIfOnline(origin, GeneralHelper.getLangString("features.teleport.tp-cancel-target-origin-message").replace("%p", sender.getName()));
        }
    }

    public void teleportBack(Player player) {
        BackLocation backLocation = backLocations.get(player.getUniqueId());

        if (backLocation == null) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.teleport.tp-back-no-location"));
            return;
        }

        GeneralHelper.executePlayerTeleport(player, backLocation.location(), TELEPORT_DELAY, GeneralHelper.getLangString("features.teleport.tp-back-message"));
        backLocations.remove(player.getUniqueId());
    }

    public void teleport(String requestUUID) {
        // bring (to -> from ) | !bring (from <- to)
        // Should be scheduled for the time interval
        TeleportRequest request = requests.get(requestUUID);

        if (request == null) return;

        Player origin = Bukkit.getPlayer(request.from());
        Player target = Bukkit.getPlayer(request.to());

        if (origin == null) {
            if (target != null) {
                MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-request-origin-offline").replace("%p", getPlayerName(request.from())));
            }
            cleanupRequest(requestUUID);
            return;
        }

        // This cant happen because the target accepted the TP, we still handle it just in case.
        if (target == null) {
            MessageHelper.send(origin, GeneralHelper.getLangString("features.teleport.tp-request-target-offline").replace("%p", getPlayerName(request.to())));
            cleanupRequest(requestUUID);
            return;
        }

        cleanupRequest(requestUUID);

        Location targetLocation = request.bring() ? origin.getLocation() : target.getLocation();
        String unsafePlayerName = (request.bring() ? origin.getName() : target.getName());

        if (!TeleportUtils.isSafe(targetLocation)) {
            String unsafeMsg = GeneralHelper.getLangString("features.teleport.tp-request-unsafe-location").replace("%p", unsafePlayerName);
            MessageHelper.send(origin, unsafeMsg);
            MessageHelper.send(target, unsafeMsg);
            return;
        }

        if (request.bring()) {
            // Teleport target to origin (from) location.
            saveBackLocation(target);
            GeneralHelper.executePlayerTeleport(target, targetLocation, TELEPORT_DELAY);
        } else {
            saveBackLocation(origin);
            GeneralHelper.executePlayerTeleport(origin, targetLocation, TELEPORT_DELAY);
        }
    }

    private void cleanupRequest(String requestUUID) {
        requests.remove(requestUUID);
        cancelExpirationTask(requestUUID);
    }

    private void cancelExpirationTask(String requestUUID) {
        Integer activeTaskID = activeTasks.remove(requestUUID);
        if (activeTaskID != null) {
            SchedulerHelper.cancelTask(activeTaskID);
        }
    }

    private void sendIfOnline(Player player, String message) {
        if (player != null && player.isOnline()) {
            MessageHelper.send(player, message);
        }
    }

    private String getPlayerName(UUID playerUUID) {
        String playerName = Bukkit.getOfflinePlayer(playerUUID).getName();
        return playerName == null ? "desconocido" : playerName;
    }

    public void saveBackLocation(Player player) {
        UUID backLocationID = GeneralHelper.generateUUID();

        backLocations.put(player.getUniqueId(), new BackLocation(backLocationID, player.getLocation()));

        // Send clickable message
        Component message = Component.text(GeneralHelper.getLangString("features.teleport.tp-on-back-available-message"))
                .color(NamedTextColor.GRAY)
                .append(Component.text(GeneralHelper.getLangString("features.teleport.tp-on-back-available-action"))
                        .color(NamedTextColor.GREEN)
                        .decorate(TextDecoration.BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp back"))
                        .hoverEvent(HoverEvent.showText(Component.text(GeneralHelper.getLangString("features.teleport.tp-on-back-available-action-description")))))
                .append(Component.text(GeneralHelper.getLangString("features.teleport.tp-on-back-available-action-description-alt")).color(NamedTextColor.GRAY));

        player.sendMessage(message);

        // Schedule expiration
        SchedulerHelper.scheduleTask(null, () -> {
            BackLocation currentBackLocation = backLocations.get(player.getUniqueId());

            if(currentBackLocation != null && currentBackLocation.id().equals(backLocationID)) {
                backLocations.remove(player.getUniqueId());
                MessageHelper.send(player, GeneralHelper.getLangString("features.teleport.tp-back-expired"));
            }
        }, 300);
    }

    public String getLatestTeleportRequestFrom(Player player) {
        // No UUID provided, find latest request sent by the player
        Optional<TeleportRequest> latest = requests.values().stream()
                .filter(r -> r.from().equals(player.getUniqueId()))
                .max(Comparator.comparing(TeleportRequest::cdate));

        if (latest.isEmpty()) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.teleport.tp-get-latest-tp-request"));
            return "";
        }

        return latest.get().requestUUID();
    }

    public String getLatestTeleportRequest(Player player) {
        // No UUID provided, find latest request
        Optional<TeleportRequest> latest = requests.values().stream()
                .filter(r -> r.to().equals(player.getUniqueId()))
                .max(Comparator.comparing(TeleportRequest::cdate));

        if (latest.isEmpty()) {
            return "";
        }

        return latest.get().requestUUID();
    }

    private record BackLocation(UUID id, Location location) {
    }
}
