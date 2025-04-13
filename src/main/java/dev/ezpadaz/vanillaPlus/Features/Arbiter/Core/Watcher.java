package dev.ezpadaz.vanillaPlus.Features.Arbiter.Core;

import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Server;

public class Watcher {
    public static String arbiterPrefix = "&6[&5Arbiter&6] ";
    private static Watcher instance;

    public static boolean watcherDebugMode = false;

    private Integer INTERNAL_SCHEDULER_TASK_ID;
    private Integer INTERNAL_NUKE_TASK_ID;

    public double MIN_TPS;
    public double WARNING_TPS;
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

    public void startProtection() {
        if (INTERNAL_SCHEDULER_TASK_ID == null) {
            INTERNAL_SCHEDULER_TASK_ID = SchedulerHelper.scheduleRepeatingTask(null, () -> {

                if (INTERNAL_NUKE_TASK_ID != null) return; // prevents further execution since a nuke is scheduled.

                double[] AVERAGE_TPS = VanillaPlus.getInstance().getServer().getTPS();

                if(watcherDebugMode){
                    consoleHealthLog();
                }

                if (AVERAGE_TPS[0] <= WARNING_TPS) {
                    MessageHelper.global(arbiterPrefix + "Se ha detectado una latencia, se encuentra en: " + AVERAGE_TPS[0]);
                    MessageHelper.global(arbiterPrefix + "Si continua en los proximos 5 minutos, programare un reinicio.");
                }


                if (AVERAGE_TPS[1] <= MIN_TPS && AVERAGE_TPS[0] <= WARNING_TPS) {
                    // Something is rotting since TPS for 1 minute is below warning and the average 5 minute TPS is bad.
                    // Schedule a restart, let the server do its thing.
                    if (INTERNAL_NUKE_TASK_ID != null) {
                        alertServer();
                        INTERNAL_SCHEDULER_TASK_ID = SchedulerHelper.scheduleTask("panic::restart", this::panic, 30);
                    }
                }

            }, 30, INTERVAL);
        }
    }

    private void panic() {
        VanillaPlus.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        VanillaPlus.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "restart");
    }

    private void alertServer() {
        MessageHelper.global(arbiterPrefix + "&6El rendimiento ha caido por debajo del limite aceptable, un reinicio ha sido programado en &c30 segundos");
    }

    private void consoleHealthLog() {
        double[] tps = VanillaPlus.getInstance().getServer().getTPS();
        MessageHelper.console(arbiterPrefix + "&6Current TPS: " + tps[0]);
    }

    public void reloadWatcher() {
        if (INTERNAL_SCHEDULER_TASK_ID == null) {
            initialize();
        } else {
            SchedulerHelper.cancelTask(INTERNAL_SCHEDULER_TASK_ID);
            INTERNAL_SCHEDULER_TASK_ID = null;
            initialize();
        }
        MessageHelper.console(arbiterPrefix + "&6la configuracion fue recargada.");
    }

    public void stopWatcher() {
        if (INTERNAL_SCHEDULER_TASK_ID != null) {
            SchedulerHelper.cancelTask(INTERNAL_SCHEDULER_TASK_ID);
            INTERNAL_SCHEDULER_TASK_ID = null;
            if (INTERNAL_NUKE_TASK_ID != null) {
                SchedulerHelper.cancelTask(INTERNAL_NUKE_TASK_ID);
                INTERNAL_NUKE_TASK_ID = null;
            }
            MessageHelper.console(arbiterPrefix + "&cel servicio de rendimiento ha sido apagado.");
        }
    }

    public double[] getTPS() {
        return VanillaPlus.getInstance().getServer().getTPS();
    }

    public String getStatus() {
        double[] tps = VanillaPlus.getInstance().getServer().getTPS();

        if (tps[1] <= MIN_TPS) {
            return "&cBAD";
        }

        if (tps[0] <= WARNING_TPS) {
            return "&eUNSTABLE";
        }

        if(INTERNAL_SCHEDULER_TASK_ID != null){
            return "&aSTABLE";
        }

        return "&cDISABLED";
    }

    public Integer getTaskID() {
        return INTERNAL_SCHEDULER_TASK_ID;
    }
}
