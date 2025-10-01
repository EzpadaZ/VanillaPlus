package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import com.mojang.brigadier.Message;
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
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class TeleportManager {
    private static final TeleportManager INSTANCE = new TeleportManager();

    private final Map<UUID, Location> backLocations = new HashMap<>();
    private final Map<String, TeleportRequest> requests = new HashMap<>();
    private final Map<String, Integer> activeTasks = new HashMap<>();

    private int TELEPORT_THRESHOLD = 0;
    private int TELEPORT_DELAY = 0;
    private boolean TELEPORT_COST_EXP = false;

    private TeleportManager() {

    }

    public static TeleportManager getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        TELEPORT_DELAY = GeneralHelper.getConfigInt("features.teleport.delay");
        TELEPORT_THRESHOLD = GeneralHelper.getConfigInt("features.teleport.threshold");
        TELEPORT_COST_EXP = GeneralHelper.getConfigBool("features.teleport.should-cost-exp");
    }

    public void clearQueue() {
        for (Map.Entry<String, TeleportRequest> entry : requests.entrySet()) {
            String requestUUID = entry.getKey();
            Integer activeTaskID = activeTasks.remove(requestUUID);
            if (activeTaskID != null) {
                SchedulerHelper.cancelTask(activeTaskID);
                MessageHelper.consoleDebug("Cancelled " + activeTaskID);
            }
        }
        requests.clear();
    }

    public void sendRequest(Player from, Player to, boolean bring) {
        if (from == to) {
            MessageHelper.send(from, GeneralHelper.getLangString("features.teleport.tp-self"));
            return;
        }

        UUID requestUUID = GeneralHelper.generateUUID();

        TeleportRequest request = new TeleportRequest(requestUUID.toString(), from.getUniqueId(), to.getUniqueId(), bring, GeneralHelper.toISOString(GeneralHelper.getISODate()));

        if (!TeleportUtils.isSafe(from.getLocation()) && bring) {
            MessageHelper.send(from, GeneralHelper.getLangString("features.teleport.tp-unsafe-location"));
            return;
        }

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
            requests.remove(requestUUID.toString());

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

        Integer activeTaskID = activeTasks.remove(teleportID);
        if (activeTaskID != null) {
            SchedulerHelper.cancelTask(activeTaskID);
            //MessageHelper.consoleDebug("Cancelled " + activeTaskID);
        }

        Player origin = Bukkit.getPlayer(request.from());
        MessageHelper.send(origin, GeneralHelper.getLangString("features.teleport.tp-accept-origin-message"));
        MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-accept-target-message"));

        teleport(teleportID);
        requests.remove(teleportID);
    }

    public void cancelRequest(Player sender, String requestUUID) {
        cancelTeleportRequest(sender, requestUUID);
    }

    public void cancelRequest(Player sender) {
        String requestUUID = getLatestTeleportRequest(sender);

        if (requestUUID.isEmpty()) {
            requestUUID = getLatestTeleportRequestFrom(sender);
        }

        cancelTeleportRequest(sender, requestUUID);
    }

    private void cancelTeleportRequest(Player sender, String requestUUID) {
        TeleportRequest request = requests.get(requestUUID);

        if (request == null) {
            MessageHelper.send(sender, GeneralHelper.getLangString("features.teleport.tp-cancel-no-request"));
            return;
        }

        Player origin = Bukkit.getPlayer(request.from());
        Player target = Bukkit.getPlayer(request.to());

        requests.remove(requestUUID);

        Integer activeTaskID = activeTasks.remove(requestUUID);
        if (activeTaskID != null) {
            SchedulerHelper.cancelTask(activeTaskID);
            //MessageHelper.consoleDebug("Cancelled " + activeTaskID);
        }


        if (origin == sender) {
            // The sender (origin) is cancelling the TP, send the appropiate message.
            MessageHelper.send(sender, GeneralHelper.getLangString("features.teleport.tp-cancel-origin-message"));
            MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-cancel-origin-target-message").replace("%p", sender.getName()));
        } else {
            MessageHelper.send(sender, GeneralHelper.getLangString("features.teleport.tp-cancel-target-message"));
            MessageHelper.send(origin, GeneralHelper.getLangString("features.teleport.tp-cancel-target-origin-message").replace("%p", sender.getName()));
        }
    }

    public void teleportBack(Player player) {
        Location location = backLocations.get(player.getUniqueId());

        if (location == null) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.teleport.tp-back-no-location"));
            return;
        }

        GeneralHelper.executePlayerTeleport(player, location, TELEPORT_DELAY, GeneralHelper.getLangString("features.teleport.tp-back-message"));
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
                MessageHelper.send(target, GeneralHelper.getLangString("features.teleport.tp-request-target-offline").replace("%p", origin.getName()));
            }
            return;
        }

        // This cant happen because the target accepted the TP, we still handle it just in case.
        if (target == null) {
            MessageHelper.send(origin, GeneralHelper.getLangString("features.teleport.tp-request-target-offline").replace("%p", target.getName()));
            return;
        }

        Integer taskID = activeTasks.remove(requestUUID);
        if (taskID != null) {
            SchedulerHelper.cancelTask(taskID);
        }

        requests.remove(requestUUID);

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

    public void saveBackLocation(Player player) {
        if (backLocations.containsKey(player.getUniqueId())) {
            backLocations.remove(player.getUniqueId());
        }

        backLocations.put(player.getUniqueId(), player.getLocation());

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
            Location tempLocation = backLocations.remove(player.getUniqueId());

            if(tempLocation != null) {
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
}
