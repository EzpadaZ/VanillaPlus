package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.BOLD;

public class TeleportManager {
    private static final TeleportManager INSTANCE = new TeleportManager();

    private final Map<UUID, Location> backLocations = new HashMap<>();
    private final Map<UUID, TeleportRequest> requests = new HashMap<>();

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
        MessageHelper.console("TeleportManager initialized.");
    }

    public void sendRequest(Player from, Player to, boolean bring) {
        TeleportRequest request = new TeleportRequest(from.getUniqueId(), to.getUniqueId(), bring);

        if (!TeleportUtils.isSafe(from.getLocation())) {
            MessageHelper.send(from, "&cTu ubicacion no es segura");
            return;
        }

        requests.put(to.getUniqueId(), request);

        String actionText = bring
                ? from.getName() + " quiere que vayas a su ubicación. "
                : from.getName() + " quiere ir contigo. ";

        Component message = text(actionText).color(GRAY)
                .append(text("[Aceptar]").color(GREEN).decorate(BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp accept"))
                        .hoverEvent(HoverEvent.showText(text("Aceptar solicitud"))))
                .append(text(" "))
                .append(text("[Rechazar]").color(RED).decorate(BOLD)
                        .clickEvent(ClickEvent.runCommand("/tp cancel"))
                        .hoverEvent(HoverEvent.showText(text("Rechazar solicitud"))));

        MessageHelper.console("To: " + to.getName());
        MessageHelper.console("From: " + from.getName());

        to.sendMessage(message);

        // TODO: Implement saving the scheduler task ID
        // It needs to check prior to deleting something if it wasnt previously deleted.

        SchedulerHelper.scheduleTask(null, () -> {
            requests.remove(to.getUniqueId());
            MessageHelper.send(to, "&cLa solicitud de &6" + from.getName() + "&c ha caducado.");
        }, 20L * TELEPORT_THRESHOLD);
    }

    public void acceptRequest(Player target) {
        TeleportRequest request = requests.get(target.getUniqueId());
        if (request == null) {
            MessageHelper.send(target, "&cNo tienes ninguna solicitud.");
            return;
        }

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

