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

    public static int scheduleRepeatingTask(String name, Runnable task, long delay, long period) {
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(VanillaPlus.getInstance(), task, delay * 20L, period * 20L);
        if (name != null && !name.isEmpty()) tasks.put(name, taskId);
        return taskId;
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
