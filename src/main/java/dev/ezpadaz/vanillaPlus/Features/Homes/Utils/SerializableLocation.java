package dev.ezpadaz.vanillaPlus.Features.Homes.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializableLocation {
    public String world;
    public double x, y, z;
    public float yaw, pitch;

    public SerializableLocation() {} // Required for Gson

    public SerializableLocation(Location loc) {
        this.world = loc.getWorld().getName();
        this.x = loc.getX();
        this.y = loc.getY();
        this.z = loc.getZ();
        this.yaw = loc.getYaw();
        this.pitch = loc.getPitch();
    }

    public Location toBukkitLocation() {
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }
}