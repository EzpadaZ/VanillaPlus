package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import dev.ezpadaz.vanillaPlus.Utils.EffectHelper;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
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

    public void sendRequest(Player from, Player to, boolean bring) {
        if (from == to) {
            MessageHelper.send(from, "&cNo te puedes enviar solicitudes a ti mismo.");
            return;
        }

        UUID requestUUID = GeneralHelper.generateUUID();

        TeleportRequest request = new TeleportRequest(requestUUID.toString(), from.getUniqueId(), to.getUniqueId(), bring, GeneralHelper.toISOString(GeneralHelper.getISODate()));

        if (!TeleportUtils.isSafe(from.getLocation()) && bring) {
            MessageHelper.send(from, "&cTu ubicacion no es segura");
            return;
        }

        requests.put(requestUUID.toString(), request);

        String actionText = bring
                ? from.getName() + " quiere que vayas a su ubicación. "
                : from.getName() + " quiere ir contigo. ";

        Component message = text(actionText + " ", GRAY).append(Component.newline()) // add space here
                .append(text("[Aceptar]", GREEN, BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp accept " + requestUUID))
                        .hoverEvent(HoverEvent.showText(text("Aceptar solicitud")))).append(space()).append(space())
                .append(text("[Rechazar]", RED, BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp cancel " + requestUUID))
                        .hoverEvent(HoverEvent.showText(text("Rechazar solicitud"))));

        to.sendMessage(message);

        Integer teleportTaskID = SchedulerHelper.scheduleTask(requestUUID.toString(), () -> {
            MessageHelper.console("Request deleted");
            requests.remove(requestUUID.toString());

            if (to.isOnline()) {
                MessageHelper.send(to, "&cLa solicitud de &6" + from.getName() + "&c ha caducado.");
            }

            if (from.isOnline()) {
                MessageHelper.send(from, "&cLa solicitud hacia &6" + to.getName() + "&c ha caducado.");
            }
        }, TELEPORT_THRESHOLD);

        activeTasks.put(requestUUID.toString(), teleportTaskID);
    }

    public void acceptRequest(Player target, String teleportID) {
        TeleportRequest request = requests.get(teleportID);
        if (request == null) {
            MessageHelper.send(target, "&cNo tienes ninguna solicitud.");
            return;
        }

        Integer activeTaskID = activeTasks.remove(teleportID);
        if (activeTaskID != null) {
            SchedulerHelper.cancelTask(activeTaskID);
            MessageHelper.consoleDebug("Cancelled " + activeTaskID);
        }

        teleport(teleportID);
        requests.remove(teleportID);
    }

    public void acceptRequest(Player target) {
        String requestID = getLatestTeleportRequest(target);

        if (requestID.isEmpty()) {
            return;
        }

        Integer activeTaskID = activeTasks.remove(requestID);
        if (activeTaskID != null) {
            SchedulerHelper.cancelTask(activeTaskID);
            MessageHelper.consoleDebug("Cancelled " + activeTaskID);
        }

        teleport(requestID);
        requests.remove(requestID);
    }

    public void cancelRequest(Player sender, String requestUUID) {
        TeleportRequest request = requests.get(requestUUID);
        if (request == null) {
            MessageHelper.send(sender, "&cNo tienes ninguna solicitud para cancelar.");
            return;
        }

        requests.remove(requestUUID);

        Integer activeTaskID = activeTasks.remove(requestUUID);
        if (activeTaskID != null) {
            SchedulerHelper.cancelTask(activeTaskID);
            MessageHelper.consoleDebug("Cancelled " + activeTaskID);
        }

        Player origin = Bukkit.getPlayer(request.from());


        MessageHelper.send(sender, "&cMandaste a chingar a su madre a " + Bukkit.getOfflinePlayer(request.from()).getName() + ".");

        if (origin != null) {
            MessageHelper.send(origin, "&6" + sender.getName() + "&c canceló el viaje.");
        }
    }

    public void cancelRequest(Player sender) {
        String requestUUID = getLatestTeleportRequest(sender);

        TeleportRequest request = requests.get(requestUUID);
        if (request == null) {
            MessageHelper.send(sender, "&cNo tienes ninguna solicitud para cancelar.");
            return;
        }

        requests.remove(requestUUID);

        Integer activeTaskID = activeTasks.remove(requestUUID);
        if (activeTaskID != null) {
            SchedulerHelper.cancelTask(activeTaskID);
            MessageHelper.consoleDebug("Cancelled " + activeTaskID);
        }

        Player origin = Bukkit.getPlayer(request.from());


        MessageHelper.send(sender, "&cMandaste a chingar a su madre a " + Bukkit.getOfflinePlayer(request.from()).getName() + ".");

        if (origin != null) {
            MessageHelper.send(origin, "&6" + sender.getName() + "&c canceló el viaje.");
        }
    }

    public void teleportBack(Player player) {
        Location location = backLocations.get(player.getUniqueId());

        if (location == null) {
            MessageHelper.send(player, "&cNo tienes a donde volver.");
            return;
        }

        GeneralHelper.executePlayerTeleport(player, location, TELEPORT_DELAY);
        backLocations.remove(player.getUniqueId());
        MessageHelper.send(player, "&6Has vuelto a tu ubicacion anterior.");
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
                MessageHelper.send(target, "&cEl jugador " + Bukkit.getOfflinePlayer(request.from()).getName() + " está offline.");
            }
            return;
        }

        // This cant happen because the target accepted the TP, we still handle it just in case.
        if (target == null) {
            MessageHelper.send(origin, "&cEl jugador " + Bukkit.getOfflinePlayer(request.to()).getName() + " está offline.");
            return;
        }

        Integer taskID = activeTasks.remove(requestUUID);
        if (taskID != null) {
            SchedulerHelper.cancelTask(taskID);
        }

        requests.remove(requestUUID);

        Location targetLocation = request.bring() ? origin.getLocation() : target.getLocation();

        if (!TeleportUtils.isSafe(targetLocation)) {
            String unsafeMsg = "&aLa ubicacion de " + (request.bring() ? origin.getName() : target.getName()) +
                    " &cno es segura &aen este momento, he cancelado el tp.";
            MessageHelper.send(origin, unsafeMsg);
            MessageHelper.send(target, unsafeMsg);
            return;
        }

        if (request.bring()) {
            // Teleport target to origin (from) location.
            GeneralHelper.executePlayerTeleport(target, targetLocation, TELEPORT_DELAY);
            MessageHelper.send(target, "&aSolicitud de viaje completada.");
        } else {
            GeneralHelper.executePlayerTeleport(origin, targetLocation, TELEPORT_DELAY);
            MessageHelper.send(origin, "&aSolicitud de viaje completada.");
        }
    }

    public void saveBackLocation(Player player) {
        backLocations.put(player.getUniqueId(), player.getLocation());
    }

    public String getLatestTeleportRequest(Player player) {
        // No UUID provided, find latest request
        Optional<TeleportRequest> latest = requests.values().stream()
                .filter(r -> r.to().equals(player.getUniqueId()))
                .max(Comparator.comparing(TeleportRequest::cdate));

        if (latest.isEmpty()) {
            MessageHelper.send(player, "&cNo tienes solicitudes pendientes.");
            return "";
        }

        return latest.get().requestUUID();
    }
}

