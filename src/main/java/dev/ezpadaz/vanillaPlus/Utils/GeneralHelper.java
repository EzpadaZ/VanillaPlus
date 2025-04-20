package dev.ezpadaz.vanillaPlus.Utils;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.ImmutableList;
import de.slikey.effectlib.util.RandomUtils;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GeneralHelper {
    public static void registerCommand(BaseCommand command) {
        VanillaPlus.getInstance().commandManager.registerCommand(command);
    }

    public static void registerCompleter(String completerID, List<String> list) {
        VanillaPlus.getInstance().commandManager.getCommandCompletions().registerCompletion(completerID, c -> ImmutableList.of(list.toString()));
    }

    public static void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, VanillaPlus.getInstance());
    }

    public static Player getPlayer(String name) {
        return VanillaPlus.getInstance().getServer().getPlayer(name);
    }

    public static String generateUUIDString() {
        long timestamp = Instant.now().toEpochMilli();
        UUID randomUUID = UUID.randomUUID();
        return String.format("%d-%s", timestamp, randomUUID);
    }

    public static UUID generateUUID() {
        long timestamp = Instant.now().toEpochMilli();
        UUID randomUUID = UUID.randomUUID();
        return randomUUID;
    }

    public static double formatDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String formattedValue = decimalFormat.format(value);
        return Double.parseDouble(formattedValue);
    }


    public static Date getISODate() {
        ZonedDateTime now = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String formattedDate = now.format(formatter);

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return isoFormat.parse(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace(); // Handle the exception according to your needs
            return null;
        }
    }

    public static void playSound(Sound sound, Location location) {
        SchedulerHelper.runTask(() ->
                location.getWorld().playSound(
                        location,
                        sound,
                        4.0F,
                        (1.0F + (RandomUtils.random.nextFloat() - RandomUtils.random.nextFloat()) * 0.2F) * 0.7F
                ));
    }

    public static void executePlayerTeleport(Player target, Location location, int delay) {
        EffectHelper effectManager = EffectHelper.getInstance();
        effectManager.smokeEffect(target, delay + 1);
        GeneralHelper.playSound(Sound.BLOCK_BEACON_POWER_SELECT, target.getLocation());
        SchedulerHelper.scheduleTask(null, () -> {
            effectManager.strikeLightning(target.getLocation());
            effectManager.explodeEffect(target);
            effectManager.smokeExplosionEffect(target);

            target.setFallDistance(0f);
            target.setInvulnerable(true);
            target.setGlowing(true);

            // teleport the player.
            try {
                target.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
            } catch (Exception ignored) {
                MessageHelper.console("Teleport failed with error: " + ignored.getMessage());
            }

            // End effects on the other side.
            effectManager.strikeLightning(target.getLocation());
            effectManager.explodeEffect(target);
            effectManager.smokeExplosionEffect(target);

            SchedulerHelper.scheduleTask(null, () -> {
                target.setInvulnerable(false);
                target.setGlowing(false);
            }, 2);

        }, delay);
    }

    public static void playSound(String namespacedSound, Location location, float volume, float pitchVariation) {
        Random random = new Random();
        NamespacedKey key = NamespacedKey.minecraft(namespacedSound.toLowerCase(Locale.ROOT));
        Sound sound = Registry.SOUNDS.get(key);
        if (sound == null) return;

        float pitch = 1.0F + (random.nextFloat() - 0.5F) * pitchVariation;

        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public static void playSound(Sound sound, Location location, float volume, float pitch) {
        SchedulerHelper.runTask(() ->
                location.getWorld().playSound(
                        location,
                        sound,
                        volume,
                        pitch
                ));
    }

    public static String toISOString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static double ISODateDifferenceInMinutes(String startTime, String endTime) {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date startDate = isoFormat.parse(startTime);
            Date endDate = isoFormat.parse(endTime);

            long duration = endDate.getTime() - startDate.getTime();
            return TimeUnit.MILLISECONDS.toMinutes(duration);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isPlayerAllowed(Player player) {
        String ownerName = getConfigString("owner");
        return (player.isOp() || player.getName().equalsIgnoreCase(ownerName));
    }

    public static boolean getConfigBool(String path) {
        return VanillaPlus.getInstance().getConfig().getBoolean(path);
    }

    public static String getConfigString(String path) {
        return VanillaPlus.getInstance().getConfig().getString(path);
    }

    public static double getConfigDouble(String path) {
        return VanillaPlus.getInstance().getConfig().getDouble(path);
    }

    public static int getConfigInt(String path) {
        return VanillaPlus.getInstance().getConfig().getInt(path);
    }

    public static boolean isPluginPresent(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return (plugin != null && plugin.isEnabled());
    }
}
