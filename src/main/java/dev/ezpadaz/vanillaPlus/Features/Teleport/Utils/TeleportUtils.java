package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class TeleportUtils {
    public static boolean isSafe(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Material atFeet = world.getBlockAt(x, y, z).getType();
        Material below = world.getBlockAt(x, y - 1, z).getType();
        Material atHead = world.getBlockAt(x, y + 1, z).getType();

        if (atFeet == Material.AIR && below == Material.AIR && world.getBlockAt(x, y - 2, z).getType() == Material.AIR) {
            return false; // Falling trap
        }

        if ((atFeet == Material.LAVA || atFeet == Material.LAVA_CAULDRON) && !atFeet.isSolid() || below == Material.LAVA) {
            return false; // Lava hazard
        }

        if (atFeet.isSolid() && atHead.isSolid()) {
            return false; // Not enough space
        }

        return !atFeet.isAir() || !below.isAir();
    }
}
