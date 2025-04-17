package dev.ezpadaz.vanillaPlus.Features.DeathChest.Manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.ezpadaz.vanillaPlus.Features.DeathChest.Model.GraveData;
import dev.ezpadaz.vanillaPlus.Utils.*;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class DeathManager {
    private static final Map<Location, GraveData> graveyard = new HashMap<>();
    private static final File dataFile = new File(VanillaPlus.getInstance().getDataFolder(), "data/deaths/graves.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void spawnGrave(Player player) {
        Location loc = player.getLocation().getBlock().getLocation(); // snap to block

        loc.getBlock().setType(Material.PLAYER_HEAD);
        Skull skull = (Skull) loc.getBlock().getState();
        skull.setOwningPlayer(player);
        skull.update(); // apply the skull change

        ItemStack[] armor = new ItemStack[] {
                player.getInventory().getBoots(),
                player.getInventory().getLeggings(),
                player.getInventory().getChestplate(),
                player.getInventory().getHelmet()
        };

        String contents = InventoryHelper.toBase64(player.getInventory().getStorageContents());
        String armorString = InventoryHelper.toBase64(armor);
        String offhand = InventoryHelper.toBase64(new ItemStack[]{player.getInventory().getItemInOffHand()});

        graveyard.put(loc, new GraveData(
                player.getUniqueId(),
                contents,
                armorString,
                offhand,
                ExperienceHelper.getPlayerExp(player),
                GeneralHelper.toISOString(GeneralHelper.getISODate())
        ));
    }

    public static void sendGraveInfo(Player viewer, Location location) {
        GraveData data = graveyard.get(location);
        if (data == null) return;

        if(data.getDate() == null || data.getDate().isEmpty()) return;

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcDate = isoFormat.parse(data.getDate());

            SimpleDateFormat localFormat = new SimpleDateFormat("dd 'de' MMMM 'a las' HH:mm z");
            localFormat.setTimeZone(TimeZone.getDefault());

            String localTime = localFormat.format(utcDate);
            String playerName = Bukkit.getOfflinePlayer(data.getPlayerId()).getName();

            MessageHelper.send(viewer, "&7Esta tumba le pertenece a &e" + playerName +
                    "&7, murió el &e" + localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void removeGrave(Location loc) {
        graveyard.remove(loc);
        EffectHelper.getInstance().smokeExplosionEffect(loc);
        EffectHelper.getInstance().strikeLightning(loc);
    }

    public static void saveGravesToFile() {
        try {
            dataFile.getParentFile().mkdirs();
            Map<String, GraveData> toSave = new HashMap<>();
            for (Map.Entry<Location, GraveData> entry : graveyard.entrySet()) {
                toSave.put(serializeLocation(entry.getKey()), entry.getValue());
            }
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(toSave, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGravesFromFile() {
        if (!dataFile.exists()) {
            MessageHelper.console("&6Graveyard Data: &c[EMPTY]");
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, GraveData>>() {
            }.getType();
            Map<String, GraveData> loaded = gson.fromJson(reader, type);
            for (Map.Entry<String, GraveData> entry : loaded.entrySet()) {
                graveyard.put(deserializeLocation(entry.getKey()), entry.getValue());
            }
            MessageHelper.console("&6Graveyard Data: &a[OK]");
        } catch (IOException e) {
            e.printStackTrace();
            MessageHelper.console("&6Graveyard Data: &c[ERROR]");
        }
    }

    private static String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private static Location deserializeLocation(String str) {
        String[] parts = str.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    public static boolean isGrave(Location location) {
        return graveyard.keySet().stream().anyMatch(loc -> loc.getWorld().equals(location.getWorld()) && loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ());
    }

    public static void restoreGrave(Player player, Location location) {
        GraveData data = graveyard.get(location);
        if (data == null) {
            MessageHelper.send(player, "&cNo se encontró una tumba en esta ubicación.");
            return;
        }

        if (!data.getPlayerId().equals(player.getUniqueId())) {
            MessageHelper.send(player, "&cNo puedes reclamar una tumba que no te pertenece.");
            return;
        }

        try {
            ItemStack[] contents = InventoryHelper.itemStackArrayFromBase64(data.getContents());
            ItemStack[] armor = InventoryHelper.itemStackArrayFromBase64(data.getArmor());
            ItemStack[] offhand = InventoryHelper.itemStackArrayFromBase64(data.getOffhand());

            player.getInventory().setContents(contents);

            // Armor stuff
            player.getInventory().setBoots(armor.length > 0 ? armor[0] : null);
            player.getInventory().setLeggings(armor.length > 1 ? armor[1] : null);
            player.getInventory().setChestplate(armor.length > 2 ? armor[2] : null);
            player.getInventory().setHelmet(armor.length > 3 ? armor[3] : null);

            player.getInventory().setItemInOffHand(offhand.length > 0 ? offhand[0] : null);

            ExperienceHelper.changePlayerExp(player, data.getTotalExperience());
            MessageHelper.send(player, "&aTu inventario ha sido restaurado desde la tumba.");
        } catch (IOException e) {
            MessageHelper.send(player, "&cError al restaurar el inventario.");
            e.printStackTrace();
            return;
        }

        removeGrave(location);
        location.getBlock().setType(Material.AIR);
    }
}
