package dev.ezpadaz.vanillaPlus.Utils;

import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchedulerHelper {
    private static final Map<String, Integer> tasks = new ConcurrentHashMap<>();

    public static int scheduleTask(String name, Runnable task, long delay) {
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(VanillaPlus.getInstance(), task, delay * 20L);
        if (name != null && !name.isEmpty()) tasks.put(name, taskId);
        return taskId;
    }

    /**
     * Schedules a repeating task to run synchronously on the main server thread.
     *
     * @param name   The name of the task (used for tracking/stopping). Can be null or empty if you don't need to reference it later.
     * @param task   The {@link Runnable} task to run.
     * @param delay  The initial delay before the first execution, in seconds.
     * @param period The interval between executions, in seconds.
     * @return The Bukkit task ID assigned to this scheduled task.
     */
    public static int scheduleRepeatingTask(String name, Runnable task, long delay, long period) {
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(VanillaPlus.getInstance(), task, delay * 20L, period * 20L);
        if (name != null && !name.isEmpty()) tasks.put(name, taskId);
        return taskId;
    }

    public static void runTask(Runnable task) {
        Bukkit.getScheduler().runTask(VanillaPlus.getInstance(), task);
    }


    public static void cancelTask(String name) {
        Integer taskId = tasks.remove(name);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public static void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
        tasks.values().removeIf(id -> id == taskId); // clean name ref if exists
    }

    public static void cancelAll() {
        for (int taskId : tasks.values()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        tasks.clear();
    }
}
