package dev.ezpadaz.vanillaPlus.Features.Graveyard.Manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.ezpadaz.vanillaPlus.Features.Graveyard.Model.GraveData;
import dev.ezpadaz.vanillaPlus.Utils.*;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
        Location deathLoc = player.getLocation().getBlock().getLocation(); // snap to block
        Location graveLoc = findFirstAirAbove(deathLoc);

        if (graveLoc == null) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.graveyard.no-suitable-grave-location-message"));
            return;
        }

        EffectHelper.getInstance().strikeLightning(player);
        EffectHelper.getInstance().smokeExplosionEffect(player);

        ResolvableProfile profile = ResolvableProfile.resolvableProfile(player.getPlayerProfile());

        graveLoc.getBlock().setType(Material.PLAYER_HEAD);
        Skull skull = (Skull) graveLoc.getBlock().getState();
        skull.setProfile(profile);
        skull.update(); // apply the skull change

        String worldName = graveLoc.getWorld().getName();

        MessageHelper.send(player, GeneralHelper.getLangString("features.graveyard.placed-message")
                .replace("%l", graveLoc.getBlockX() + ", " + graveLoc.getBlockY() + ", " + graveLoc.getBlockZ())
                .replace("%w", worldName)
        );

        ItemStack[] armor = new ItemStack[]{
                player.getInventory().getBoots(),
                player.getInventory().getLeggings(),
                player.getInventory().getChestplate(),
                player.getInventory().getHelmet()
        };

        String contents = InventoryHelper.toBase64(player.getInventory().getStorageContents());
        String armorString = InventoryHelper.toBase64(armor);
        String offhand = InventoryHelper.toBase64(new ItemStack[]{player.getInventory().getItemInOffHand()});

        graveyard.put(graveLoc, new GraveData(
                player.getUniqueId(),
                contents,
                armorString,
                offhand,
                ExperienceHelper.getPlayerExp(player),
                GeneralHelper.toISOString(GeneralHelper.getISODate()),
                true
        ));
    }

    public static void sendGraveInfo(Player viewer, Location location) {
        GraveData data = graveyard.get(location);
        if (data == null) return;

        if (data.date() == null || data.date().isEmpty()) return;

        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date utcDate = isoFormat.parse(data.date());

            SimpleDateFormat localFormat = new SimpleDateFormat(GeneralHelper.getLangString("features.graveyard.date-message-pattern"));
            localFormat.setTimeZone(TimeZone.getDefault());

            String localTime = localFormat.format(utcDate);
            String playerName = Bukkit.getOfflinePlayer(data.playerId()).getName();

            int exp = data.totalExperience();
            DecimalFormat formatter = new DecimalFormat("#,###");
            String formattedExp = formatter.format(exp);

            MessageHelper.send(viewer, GeneralHelper.getLangString("features.graveyard.grave-info-message")
                    .replace("%p", playerName == null ? "UNKNOWN" : playerName)
                    .replace("%x", formattedExp)
                    .replace("%d", localTime)
                    .replace("%s", data.isLocked() ? GeneralHelper.getLangString("features.graveyard.grave-info-locked-message") : GeneralHelper.getLangString("features.graveyard.grave-info-unlocked-message"))
            );
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
            boolean fileCreated = dataFile.getParentFile().mkdirs();
            Map<String, GraveData> toSave = new HashMap<>();
            for (Map.Entry<Location, GraveData> entry : graveyard.entrySet()) {
                toSave.put(serializeLocation(entry.getKey()), entry.getValue());
            }
            try (FileWriter writer = new FileWriter(dataFile)) {
                gson.toJson(toSave, writer);
            }
            MessageHelper.console(GeneralHelper.getLangString("features.graveyard.console-save-success-message"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadGravesFromFile() {
        if (!dataFile.exists()) {
            MessageHelper.console(GeneralHelper.getLangString("features.graveyard.console-load-empty-message"));
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            Type type = new TypeToken<Map<String, GraveData>>() {
            }.getType();
            Map<String, GraveData> loaded = gson.fromJson(reader, type);
            for (Map.Entry<String, GraveData> entry : loaded.entrySet()) {
                graveyard.put(deserializeLocation(entry.getKey()), entry.getValue());
            }
            MessageHelper.console(GeneralHelper.getLangString("features.graveyard.console-load-success-message"));
        } catch (IOException e) {
            e.printStackTrace();
            MessageHelper.console(GeneralHelper.getLangString("features.graveyard.console-load-error-message"));
        }
    }

    public static void startGraveyardDeletionTask() {
        SchedulerHelper.scheduleRepeatingTask("GRAVEYARD_DELETION_TASK", () -> {
            try {
                checkAndDeleteGraveyards();
            } catch (Exception e) {
                MessageHelper.console("&6Graveyard Delete Task: &c[ERROR] " + e.getMessage());
            }
        }, 60, GeneralHelper.getConfigInt("features.graveyard.check-every-seconds"));
    }

    public static void stopGraveyardDeletionTask() {
        SchedulerHelper.cancelTask("GRAVEYARD_DELETION_TASK");
    }

    private static void checkAndDeleteGraveyards() throws ParseException {
        // Iterate over graveyard map, check date and compare if time exceeds.
        int seconds = GeneralHelper.getConfigInt("features.graveyard.delete-after-seconds");
        MessageHelper.consoleDebug("Checking for expired graves.");

        for (Map.Entry<Location, GraveData> entry : graveyard.entrySet()) {
            GraveData data = entry.getValue();

            // check if already unlocked.
            boolean locked = data.isLocked() == null || data.isLocked();
            if (!locked) {
                continue;
            }

            // Check date
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Date inputDate = format.parse(data.date());

            long diffMillis = System.currentTimeMillis() - inputDate.getTime();
            long diffSeconds = diffMillis / 1000;

            if (diffSeconds >= seconds) {
                // mark unlocked for anyone to claim
                // replace skin with skull.
                Location graveLoc = entry.getKey();
                GraveData unlocked = new GraveData(
                        data.playerId(),
                        data.contents(),
                        data.armor(),
                        data.offhand(),
                        data.totalExperience(),
                        data.date(),
                        false // isLocked
                );

                World w = graveLoc.getWorld();
                int cx = graveLoc.getBlockX() >> 4;
                int cz = graveLoc.getBlockZ() >> 4;

                entry.setValue(unlocked); // save as unlocked grave.
                if (w != null && w.isChunkLoaded(cx, cz)) {
                    graveLoc.getBlock().setType(Material.SKELETON_SKULL);
                    EffectHelper.getInstance().strikeLightning(graveLoc);
                }
            }
        }
    }

    private static String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ();
    }

    private static Location deserializeLocation(String str) {
        String[] parts = str.split(",");
        return new Location(Bukkit.getWorld(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
    }

    private static Location findFirstAirAbove(Location loc) {
        World world = loc.getWorld();
        int startY = loc.getBlockY();

        for (int y = startY; y < world.getMaxHeight(); y++) {
            Location check = new Location(world, loc.getX(), y, loc.getZ());
            if (check.getBlock().getType() == Material.AIR) {
                return check;
            }
        }
        return null;
    }

    public static boolean isGrave(Location location) {
        return graveyard.keySet().stream().anyMatch(loc -> loc.getWorld().equals(location.getWorld()) && loc.getBlockX() == location.getBlockX() && loc.getBlockY() == location.getBlockY() && loc.getBlockZ() == location.getBlockZ());
    }

    public static void restoreGrave(Player player, Location location) {
        GraveData data = graveyard.get(location);
        if (data == null) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.graveyard.location-not-found"));
            return;
        }

        if (!data.playerId().equals(player.getUniqueId())) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.graveyard.location-wrong-owner-message"));
            return;
        }

        try {
//            // Main contents
//            ItemStack[] contents = InventoryHelper.itemStackArrayFromBase64(data.contents());
//            for (ItemStack item : contents) {
//                if (item == null) continue;
//                HashMap<Integer, ItemStack> excess = player.getInventory().addItem(item);
//                for (ItemStack leftover : excess.values()) {
//                    player.getWorld().dropItemNaturally(player.getLocation(), leftover);
//                }
//            }
//
//            // Armor
//            ItemStack[] armor = InventoryHelper.itemStackArrayFromBase64(data.armor());
//            PlayerInventory inv = player.getInventory();
//            if (armor.length > 0) dropOrEquip(player, armor[0], inv.getBoots(), inv::setBoots);
//            if (armor.length > 1) dropOrEquip(player, armor[1], inv.getLeggings(), inv::setLeggings);
//            if (armor.length > 2) dropOrEquip(player, armor[2], inv.getChestplate(), inv::setChestplate);
//            if (armor.length > 3) dropOrEquip(player, armor[3], inv.getHelmet(), inv::setHelmet);
//
//            // Offhand
//            ItemStack[] offhand = InventoryHelper.itemStackArrayFromBase64(data.offhand());
//            if (offhand.length > 0) {
//                ItemStack existing = player.getInventory().getItemInOffHand();
//                if (existing.getType() != Material.AIR) {
//                    player.getWorld().dropItemNaturally(player.getLocation(), existing);
//                }
//                player.getInventory().setItemInOffHand(offhand[0]);
//            }

            Location dropLoc = location.clone().add(0.5, 0.5, 0.5);

            // Main
            for (ItemStack item : InventoryHelper.itemStackArrayFromBase64(data.contents())) {
                if (item == null || item.getType() == Material.AIR) continue;
                player.getWorld().dropItemNaturally(dropLoc, item);
            }

            // Armor
            for (ItemStack item : InventoryHelper.itemStackArrayFromBase64(data.armor())) {
                if (item == null || item.getType() == Material.AIR) continue;
                player.getWorld().dropItemNaturally(dropLoc, item);
            }

            // Offhand
            ItemStack[] offhand = InventoryHelper.itemStackArrayFromBase64(data.offhand());
            if (offhand.length > 0 && offhand[0] != null && offhand[0].getType() != Material.AIR) {
                player.getWorld().dropItemNaturally(dropLoc, offhand[0]);
            }

            // XP
            ExperienceHelper.addPlayerExp(player, data.totalExperience());
            MessageHelper.send(player, GeneralHelper.getLangString("features.graveyard.claim-message"));

        } catch (IOException e) {
            MessageHelper.send(player, GeneralHelper.getLangString("features.graveyard.claim-error-message"));
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
