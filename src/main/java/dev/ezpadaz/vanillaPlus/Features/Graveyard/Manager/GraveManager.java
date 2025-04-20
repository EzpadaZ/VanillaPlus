package dev.ezpadaz.vanillaPlus.Features.Graveyard.Manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.ezpadaz.vanillaPlus.Features.Graveyard.Model.GraveData;
import dev.ezpadaz.vanillaPlus.Utils.*;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class GraveManager {
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

        if(data.date() == null || data.date().isEmpty()) return;

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcDate = isoFormat.parse(data.date());

            SimpleDateFormat localFormat = new SimpleDateFormat("dd 'de' MMMM 'a las' HH:mm z");
            localFormat.setTimeZone(TimeZone.getDefault());

            String localTime = localFormat.format(utcDate);
            String playerName = Bukkit.getOfflinePlayer(data.playerId()).getName();

            int exp = data.totalExperience();
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedExp = formatter.format(exp);

            MessageHelper.send(viewer, "&7Esta tumba le pertenece a &e" + playerName +
                    "&7, contiene &e" + formattedExp + " XP&7, murió el &e" + localTime);
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
            MessageHelper.console("&6Graveyard Data: &a[SAVED]");
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

        if (!data.playerId().equals(player.getUniqueId())) {
            MessageHelper.send(player, "&cNo puedes reclamar una tumba que no te pertenece.");
            return;
        }

        try {
            // Main contents
            ItemStack[] contents = InventoryHelper.itemStackArrayFromBase64(data.contents());
            for (ItemStack item : contents) {
                if (item == null) continue;
                HashMap<Integer, ItemStack> excess = player.getInventory().addItem(item);
                for (ItemStack leftover : excess.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
                }
            }

            // Armor
            ItemStack[] armor = InventoryHelper.itemStackArrayFromBase64(data.armor());
            PlayerInventory inv = player.getInventory();
            if (armor.length > 0) dropOrEquip(player, armor[0], inv.getBoots(), inv::setBoots);
            if (armor.length > 1) dropOrEquip(player, armor[1], inv.getLeggings(), inv::setLeggings);
            if (armor.length > 2) dropOrEquip(player, armor[2], inv.getChestplate(), inv::setChestplate);
            if (armor.length > 3) dropOrEquip(player, armor[3], inv.getHelmet(), inv::setHelmet);

            // Offhand
            ItemStack[] offhand = InventoryHelper.itemStackArrayFromBase64(data.offhand());
            if (offhand.length > 0) {
                ItemStack existing = player.getInventory().getItemInOffHand();
                if (existing.getType() != Material.AIR) {
                    player.getWorld().dropItemNaturally(player.getLocation(), existing);
                }
                player.getInventory().setItemInOffHand(offhand[0]);
            }

            // XP
            ExperienceHelper.addPlayerExp(player, data.totalExperience());
            MessageHelper.send(player, "&aTu &cinventario&a y tu &cexperiencia&a han sido restaurados de tu tumba.");

        } catch (IOException e) {
            MessageHelper.send(player, "&cError al restaurar la tumba.");
            e.printStackTrace();
            return;
        }

        removeGrave(location);
        location.getBlock().setType(Material.AIR);
    }

    private static void dropOrEquip(Player player, ItemStack newItem, ItemStack currentItem, java.util.function.Consumer<ItemStack> equipFunction) {
        if (newItem == null || newItem.getType() == Material.AIR) return;

        if (currentItem != null && currentItem.getType() != Material.AIR) {
            player.getWorld().dropItemNaturally(player.getLocation(), currentItem);
        }
        equipFunction.accept(newItem);
    }
}
