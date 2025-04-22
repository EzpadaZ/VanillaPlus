package dev.ezpadaz.vanillaPlus.Utils;

import dev.ezpadaz.vanillaPlus.VanillaPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageHelper {
    private static String PREFIX = "&6[&5VP&6]&f ";
    private static String separator = "&c----------------------------------";

    public static void send(CommandSender sender, String message) {
        send(sender, message, "&6[&5VP&6]&f ");
    }

    public static void send(CommandSender sender, String message, String prefix) {
        try {
            String translatedMessage = ChatColor.translateAlternateColorCodes('&', prefix + message);
            Component component = Component.text(translatedMessage).color(NamedTextColor.WHITE);
            sender.sendMessage(component);
        } catch (Exception e) {
            sendConsole("Failed to send message: " + e.getMessage());
        }
    }

    public static void sendConsole(String message) {
        String translatedMessage = ChatColor.translateAlternateColorCodes('&', PREFIX + message);
        VanillaPlus.getInstance().getServer().getConsoleSender().sendMessage(translatedMessage);
    }

    public static void global(String message) {
        Bukkit.getOnlinePlayers().forEach(player -> send(player, message));
    }

    public static void sendMultipleGlobal(String titleText, String tagText, List<String> strings) {
        global(buildMultiLineMessage(titleText, tagText, strings));
    }

    public static void sendMultipleGlobal(String titleText, String tagText, String prefix, List<String> strings) {
        global(buildMultiLineMessage(titleText, tagText, strings), prefix);
    }

    public static void sendMultipleMessage(CommandSender sender, String titleText, String tagText, List<String> strings) {
        send(sender, buildMultiLineMessage(titleText, tagText, strings));
    }

    private static String buildMultiLineMessage(String titleText, String tagText, List<String> lines) {
        String tag = formatTag(tagText);
        String title = (titleText == null || titleText.isEmpty()) ? "Informacion" : titleText;

        StringBuilder built = new StringBuilder(title + "\n" + separator + "\n");
        for (String line : lines) {
            built.append(tag).append(line).append("\n");
        }
        built.append(separator);
        return built.toString();
    }

    public static String formatTag(String tagText) {
        return (tagText == null || tagText.isEmpty()) ? "" : "&a[&e" + tagText + "&a]&6 ";
    }

    public static void global(String message, String prefix) {
        for (Player jugador : Bukkit.getServer().getOnlinePlayers()) {
            send(jugador, message, prefix);
        }
    }

    public static void consoleDebug(String message) {
        if (GeneralHelper.getConfigBool("debugMode")) {
            console(message);
        }
    }

    public static void console(String message) {
        sendConsole(message);
    }
}
