package dev.ezpadaz.vanillaPlus.Utils;

import co.aikar.commands.BaseCommand;
import com.google.common.collect.ImmutableList;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class GeneralHelper {
    public static void registerCommand(BaseCommand command) {
        VanillaPlus.getInstance().commandManager.registerCommand(command);
    }

    public static void registerCompleter(String completerID, List<String> list){
        VanillaPlus.getInstance().commandManager.getCommandCompletions().registerCompletion(completerID, c -> ImmutableList.of(list.toString()));
    }

    public static void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, VanillaPlus.getInstance());
    }

    public static String generateUUID() {
        long timestamp = Instant.now().toEpochMilli();
        UUID randomUUID = UUID.randomUUID();
        return String.format("%d-%s", timestamp, randomUUID);
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

    public static boolean isPluginPresent(String name){
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        return (plugin != null && plugin.isEnabled());
    }
}
