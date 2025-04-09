package dev.ezpadaz.vanillaPlus.Features.DoubleXP.Listener;

import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

import java.util.*;

public class DoubleXPListener implements Listener {
    private final Map<UUID, Integer> xpBuffer = new HashMap<>();
    private final Set<UUID> scheduled = new HashSet<>();

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        int multiplier = GeneralHelper.getConfigInt("features.double-xp.multiplier");
        int obtained = event.getAmount();
        int finalAmount = DoubleXP.isEventEnabled() ? obtained * multiplier : obtained;
        event.setAmount(finalAmount);


        xpBuffer.put(uuid, xpBuffer.getOrDefault(uuid, 0) + finalAmount);

        if (!scheduled.contains(uuid)) {
            scheduled.add(uuid);

            Bukkit.getScheduler().runTaskLater(VanillaPlus.getInstance(), () -> {
                int total = xpBuffer.getOrDefault(uuid, 0);

                if (DoubleXP.isPlayerOptedIn(player.getName()) && total > 0) {
                    String extra = DoubleXP.isEventEnabled() ? " &7(" + multiplier + "x)" : "";
                    MessageHelper.send(player, "Obtuviste " + total + " XP" + extra);
                }

                xpBuffer.remove(uuid);
                scheduled.remove(uuid);
            }, 10L); // wait 10 ticks (~0.5s)
        }
    }
}
