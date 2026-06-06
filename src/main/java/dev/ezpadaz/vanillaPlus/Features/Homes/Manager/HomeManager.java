package dev.ezpadaz.vanillaPlus.Features.Homes.Manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.ezpadaz.vanillaPlus.Features.Homes.Utils.HomeData;
import dev.ezpadaz.vanillaPlus.Features.Homes.Utils.SerializableLocation;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
        String normalizedHomeName = normalizeHomeName(homeName);
        if (!isValidHomeName(player, normalizedHomeName)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        List<HomeData> homes = homeMap.getOrDefault(playerId, new ArrayList<>());

        Optional<HomeData> existing = homes.stream()
                .filter(h -> h.homeName().equalsIgnoreCase(normalizedHomeName))
                .findFirst();

        SerializableLocation loc = new SerializableLocation(player.getLocation());

        if (existing.isPresent()) {
            // Update location for existing home
            homes.remove(existing.get());
            homes.add(new HomeData(playerId, normalizedHomeName, loc));
            homeMap.put(playerId, homes);
            MessageHelper.send(player, GeneralHelper.getLangString("features.homes.add-home-update-success")
                    .replace("%h", normalizedHomeName));
        } else {
            if (homes.size() >= MAX_HOMES) {
                MessageHelper.send(player, GeneralHelper.getLangString("features.homes.add-home-limit-error").replace("%l", MAX_HOMES + ""));
                return;
            }
            homes.add(new HomeData(playerId, normalizedHomeName, loc));
            homeMap.put(playerId, homes);
            MessageHelper.send(player, GeneralHelper.getLangString("features.homes.add-home-success")
                    .replace("%h", normalizedHomeName));
        }

        saveHomesToFile();
    }

    public static void deleteHome(Player player, String homeName) {
        String normalizedHomeName = normalizeHomeName(homeName);
        if (!isValidHomeName(player, normalizedHomeName)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        List<HomeData> homes = homeMap.get(playerId);

        if (homes == null || homes.isEmpty()) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.homes.delete-home-error"));
            return;
        }

        boolean removed = homes.removeIf(home -> home.homeName().equalsIgnoreCase(normalizedHomeName));

        if (removed) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.homes.delete-home-success").replace("%h", normalizedHomeName));
            if (homes.isEmpty()) {
                homeMap.remove(playerId);
            }
            saveHomesToFile();
            return;
        }

        MessageHelper.send(player, GeneralHelper.getLangString("features.homes.delete-home-not-found").replace("%h", normalizedHomeName));
    }

    public static void travelHome(Player player, String homeName) {
        String normalizedHomeName = normalizeHomeName(homeName);
        if (!isValidHomeName(player, normalizedHomeName)) {
            return;
        }

        UUID playerId = player.getUniqueId();
        List<HomeData> homes = homeMap.get(playerId);

        if (homes == null || homes.isEmpty()) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.homes.travel-home-error"));
            return;
        }

        for (HomeData home : homes) {
            if (home.homeName().equalsIgnoreCase(normalizedHomeName)) {
                Location loc = home.location().toBukkitLocation();
                if (loc.getWorld() == null) {
                    MessageHelper.send(player, GeneralHelper.getLangString("features.homes.home-world-not-found")
                            .replace("%h", home.homeName())
                            .replace("%w", home.location().world));
                    return;
                }

                // Teleport Player
                GeneralHelper.executePlayerTeleport(player, loc, TELEPORT_DELAY);

                MessageHelper.send(player, GeneralHelper.getLangString("features.homes.travel-home-success").replace("%h", home.homeName()));
                return;
            }
        }

        MessageHelper.send(player, GeneralHelper.getLangString("features.homes.travel-home-not-found").replace("%h", normalizedHomeName));
    }

    public static void saveHomesToFile() {
        File file = new File(VanillaPlus.getInstance().getDataFolder(), "data/homes/homes.json");
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(homeMap, writer);
            MessageHelper.console(GeneralHelper.getLangString("features.homes.save-success"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadHomesFromFile() {
        File file = new File(VanillaPlus.getInstance().getDataFolder(), "data/homes/homes.json");
        homeMap.clear();

        if (!file.exists()) {
            MessageHelper.console(GeneralHelper.getLangString("features.homes.load-empty"));
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<UUID, List<HomeData>>>() {
            }.getType();
            Map<UUID, List<HomeData>> loaded = gson.fromJson(reader, type);
            if (loaded != null) homeMap.putAll(loaded);
            MessageHelper.console(GeneralHelper.getLangString("features.homes.load-success"));
        } catch (IOException e) {
            e.printStackTrace();
            MessageHelper.console(GeneralHelper.getLangString("features.homes.load-error"));
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
                if (playerName != null) {
                    result.add(home.homeName() + "/" + playerName);
                }
            }
        }
        return result;
    }

    public static void adminTeleportToUserHome(Player admin, String arg) {
        if (arg == null || arg.isBlank()) {
            MessageHelper.send(admin, GeneralHelper.getLangString("features.homes.admin-teleport-format-error"));
            return;
        }

        String[] parts = arg.split("/");
        if (parts.length != 2) {
            MessageHelper.send(admin, GeneralHelper.getLangString("features.homes.admin-teleport-format-error"));
            return;
        }

        String homeName = normalizeHomeName(parts[0]);
        String targetPlayerName = parts[1].trim();

        if (homeName.isEmpty() || targetPlayerName.isEmpty()) {
            MessageHelper.send(admin, GeneralHelper.getLangString("features.homes.admin-teleport-format-error"));
            return;
        }

        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(targetPlayerName);
        UUID playerId = targetPlayer.getUniqueId();
        List<HomeData> homes = homeMap.getOrDefault(playerId, Collections.emptyList());

        for (HomeData home : homes) {
            if (home.homeName().equalsIgnoreCase(homeName)) {
                Location loc = home.location().toBukkitLocation();
                if (loc.getWorld() == null) {
                    MessageHelper.send(admin, GeneralHelper.getLangString("features.homes.home-world-not-found")
                            .replace("%h", home.homeName())
                            .replace("%w", home.location().world));
                    return;
                }

                GeneralHelper.executePlayerTeleport(admin, loc, TELEPORT_DELAY);
                MessageHelper.send(admin, GeneralHelper.getLangString("features.homes.admin-teleport-success")
                        .replace("%h", home.homeName())
                        .replace("%p", targetPlayerName));
                return;
            }
        }

        MessageHelper.send(admin, GeneralHelper.getLangString("features.homes.admin-teleport-not-found")
                .replace("%h", homeName)
                .replace("%p", targetPlayerName));
    }

    public static String normalizeHomeName(String homeName) {
        return homeName == null ? "" : homeName.trim();
    }

    public static boolean hasInvalidHomeNameCharacters(String homeName) {
        return normalizeHomeName(homeName).contains("/");
    }

    private static boolean isValidHomeName(Player player, String homeName) {
        if (homeName.isEmpty()) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.homes.home-name-required"));
            return false;
        }

        if (hasInvalidHomeNameCharacters(homeName)) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.homes.home-name-invalid"));
            return false;
        }

        return true;
    }

    static void clearHomesForTesting() {
        homeMap.clear();
    }

    static void putHomeForTesting(UUID playerId, HomeData homeData) {
        homeMap.computeIfAbsent(playerId, ignored -> new ArrayList<>()).add(homeData);
    }
}
