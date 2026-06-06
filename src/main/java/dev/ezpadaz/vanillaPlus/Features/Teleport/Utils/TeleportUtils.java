package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class TeleportUtils {
    public static boolean isSafe(Location location) {
        if (location == null) {
            return false;
        }

        World world = location.getWorld();
        if (world == null) {
            return false;
        }

        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        Block feetBlock = world.getBlockAt(x, y, z);
        Block belowBlock = world.getBlockAt(x, y - 1, z);
        Block headBlock = world.getBlockAt(x, y + 1, z);

        Material atFeet = feetBlock.getType();
        Material below = belowBlock.getType();
        Material atHead = headBlock.getType();

        if (!below.isSolid()) {
            return false;
        }

        if (!feetBlock.isPassable() || !headBlock.isPassable()) {
            return false;
        }

        if (isHazard(atFeet) || isHazard(below) || isHazard(atHead)) {
            return false;
        }

        return true;
    }

    private static boolean isHazard(Material material) {
        return material == Material.LAVA
                || material == Material.LAVA_CAULDRON
                || material == Material.FIRE
                || material == Material.SOUL_FIRE
                || material == Material.MAGMA_BLOCK
                || material == Material.CACTUS
                || material == Material.CAMPFIRE
                || material == Material.SOUL_CAMPFIRE
                || material == Material.SWEET_BERRY_BUSH
                || material == Material.POWDER_SNOW;
    }
}
