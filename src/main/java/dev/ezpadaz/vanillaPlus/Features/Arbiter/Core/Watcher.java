package dev.ezpadaz.vanillaPlus.Features.Arbiter.Core;

import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class Watcher {
    private static Watcher instance;

    private Integer INTERNAL_SCHEDULER_TASK_ID;
    private Integer INTERNAL_NUKE_TASK_ID;

    private double MIN_TPS;
    private double WARNING_TPS;
    private int INTERVAL;

    public static Watcher getInstance() {
        if (instance == null) instance = new Watcher();
        return instance;
    }

    public void initialize() {
        // Get config values.
        MIN_TPS = GeneralHelper.getConfigDouble("features.watcher.min-tps");
        WARNING_TPS = GeneralHelper.getConfigDouble("features.watcher.warning-tps");
        INTERVAL = GeneralHelper.getConfigInt("features.watcher.interval");

    }

    private void startProtection() {
        if (INTERNAL_SCHEDULER_TASK_ID == null) {
            INTERNAL_SCHEDULER_TASK_ID = SchedulerHelper.scheduleRepeatingTask(null, () -> {

                if (INTERNAL_NUKE_TASK_ID != null) return; // prevents further execution since a nuke is scheduled.

                Server server = Bukkit.getServer();
                double[] AVERAGE_TPS = server.getTPS();

                if (AVERAGE_TPS[0] <= WARNING_TPS) {
                    MessageHelper.global("Se ha detectado una latencia, se encuentra en: " + AVERAGE_TPS[0]);
                }

                // If 5 Minute Average is lower than the minimum allowed
                // And the 1 Minute Average is
                if(AVERAGE_TPS[1] <= MIN_TPS && AVERAGE_TPS[0] <= WARNING_TPS) {
                    // Ya valio verga.
                }

            }, 30, INTERVAL);

        }
    }

    private void panic() {

    }
}
