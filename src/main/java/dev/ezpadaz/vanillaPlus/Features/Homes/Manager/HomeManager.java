package dev.ezpadaz.vanillaPlus.Features.Homes.Manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.ezpadaz.vanillaPlus.Features.Homes.Utils.HomeData;
import dev.ezpadaz.vanillaPlus.Features.Homes.Utils.SerializableLocation;
import dev.ezpadaz.vanillaPlus.Utils.EffectHelper;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class HomeManager {
    private static final Map<UUID, List<HomeData>> homeMap = new HashMap<>();

    public static int MAX_HOMES = 0;
    public static int TELEPORT_DELAY = 0;

    public static void addHome(Player player, String homeName) {
        UUID playerId = player.getUniqueId();
        List<HomeData> homes = homeMap.getOrDefault(playerId, new ArrayList<>());

        Optional<HomeData> existing = homes.stream()
                .filter(h -> h.homeName().equalsIgnoreCase(homeName))
                .findFirst();

        SerializableLocation loc = new SerializableLocation(player.getLocation());

        if (existing.isPresent()) {
            // Update location for existing home
            homes.remove(existing.get());
            homes.add(new HomeData(playerId, homeName, loc));
            homeMap.put(playerId, homes);
            MessageHelper.send(player, "&aLa ubicación de tu hogar '" + homeName + "' ha sido actualizada.");
        } else {
            if (homes.size() >= MAX_HOMES) {
                MessageHelper.send(player, "&cYa has alcanzado el máximo de hogares permitidos (" + MAX_HOMES + ").");
                return;
            }
            homes.add(new HomeData(playerId, homeName, loc));
            homeMap.put(playerId, homes);
            MessageHelper.send(player, "&aTu hogar '" + homeName + "' ha sido guardado.");
        }
    }

    public static void deleteHome(Player player, String homeName) {
        UUID playerId = player.getUniqueId();
        List<HomeData> homes = homeMap.get(playerId);

        if (homes == null || homes.isEmpty()) {
            MessageHelper.send(player, "&cNo tienes hogares guardados.");
            return;
        }

        boolean removed = homes.removeIf(home -> home.homeName().equalsIgnoreCase(homeName));

        if (removed) {
            MessageHelper.send(player, "&aTu hogar '" + homeName + "' ha sido eliminado.");
            if (homes.isEmpty()) {
                homeMap.remove(playerId); // Clean map if no more homes
            } else {
                homeMap.put(playerId, homes);
            }
        } else {
            MessageHelper.send(player, "&cNo se encontró un hogar llamado '" + homeName + "'.");
        }
    }

    public static void travelHome(Player player, String homeName) {
        UUID playerId = player.getUniqueId();
        List<HomeData> homes = homeMap.get(playerId);

        if (homes == null || homes.isEmpty()) {
            MessageHelper.send(player, "&cNo tienes hogares guardados.");
            return;
        }

        for (HomeData home : homes) {
            if (home.homeName().equalsIgnoreCase(homeName)) {
                Location loc = home.location().toBukkitLocation();

                // Teleport Player
                GeneralHelper.executePlayerTeleport(player, loc, TELEPORT_DELAY);

                MessageHelper.send(player, "&aHas sido teletransportado a '" + homeName + "'.");
                return;
            }
        }

        MessageHelper.send(player, "&cNo se encontró un hogar llamado '" + homeName + "'.");
    }

    public static void saveHomesToFile() {
        File file = new File(VanillaPlus.getInstance().getDataFolder(), "data/homes/homes.json");
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(homeMap, writer);
            MessageHelper.console("&6Home Data: &a[SAVED]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadHomesFromFile() {
        File file = new File(VanillaPlus.getInstance().getDataFolder(), "data/homes/homes.json");

        if (!file.exists()) {
            MessageHelper.console("&6Home Data: &c[EMPTY]");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<UUID, List<HomeData>>>() {
            }.getType();
            Map<UUID, List<HomeData>> loaded = gson.fromJson(reader, type);
            if (loaded != null) homeMap.putAll(loaded);
            MessageHelper.console("&6Home Data: &a[OK]");
        } catch (IOException e) {
            e.printStackTrace();
            MessageHelper.console("&6Home Data: &c[ERROR]");
        }
    }

    public static List<String> getHomeNames(Player player) {
        UUID playerId = player.getUniqueId();
        List<HomeData> homes = homeMap.getOrDefault(playerId, Collections.emptyList());
        return homes.stream().map(HomeData::homeName).toList();
    }

    public static List<String> getAllHomeNamesWithPlayer() {
        List<String> result = new ArrayList<>();
        for (Map.Entry<UUID, List<HomeData>> entry : homeMap.entrySet()) {
            String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
            for (HomeData home : entry.getValue()) {
                result.add(home.homeName() + "/" + playerName);
            }
        }
        return result;
    }

    public static void adminTeleportToUserHome(Player admin, String arg) {
        // Split the input: "casa/User1"
        String[] parts = arg.split("/");
        if (parts.length != 2) {
            MessageHelper.send(admin, "&cFormato incorrecto. Usa: hogar/nombreUsuario");
            return;
        }

        String homeName = parts[0];
        String targetPlayerName = parts[1];

        // Try to get the player (online only)
        Player targetPlayer = Bukkit.getPlayerExact(targetPlayerName);
        if (targetPlayer == null) {
            return;
        }

        UUID playerId = targetPlayer.getUniqueId();
        List<HomeData> homes = homeMap.getOrDefault(playerId, Collections.emptyList());

        // Find home by name (case-insensitive)
        for (HomeData home : homes) {
            if (home.homeName().equalsIgnoreCase(homeName)) {
                Location loc = home.location().toBukkitLocation();

                // Teleport admin (sender)
                GeneralHelper.executePlayerTeleport(admin, loc, TELEPORT_DELAY);
                MessageHelper.send(admin, "&aHas sido teletransportado a '" + homeName + "' de " + targetPlayerName + ".");
                return;
            }
        }

        MessageHelper.send(admin, "&cNo se encontró un hogar llamado '" + homeName + "' para " + targetPlayerName + ".");
    }
}
