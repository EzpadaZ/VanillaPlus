package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TeleportManagerTest {
    private static final long BACK_LOCATION_EXPIRATION_TICKS = 300L * 20L;

    private ServerMock server;
    private World world;

    @BeforeEach
    void setUp() throws Exception {
        server = MockBukkit.mock();
        MockBukkit.load(VanillaPlus.class);
        world = server.addSimpleWorld("world");
        clearBackLocations();
    }

    @AfterEach
    void tearDown() throws Exception {
        clearBackLocations();
        MockBukkit.unmock();
    }

    @Test
    void olderBackLocationExpirationDoesNotRemoveNewerBackLocation() throws Exception {
        PlayerMock player = server.addPlayer("Molthor226");
        Location firstBackLocation = safeLocation(0, 64, 0);
        Location secondBackLocation = safeLocation(10, 64, 0);

        player.teleport(firstBackLocation);
        TeleportManager.getInstance().saveBackLocation(player);

        server.getScheduler().performTicks(20L);

        player.teleport(secondBackLocation);
        TeleportManager.getInstance().saveBackLocation(player);

        server.getScheduler().performTicks(BACK_LOCATION_EXPIRATION_TICKS - 20L);

        Location currentBackLocation = getSavedBackLocation(player.getUniqueId());
        assertNotNull(currentBackLocation);
        assertEquals(secondBackLocation.getBlockX(), currentBackLocation.getBlockX());
        assertEquals(secondBackLocation.getBlockY(), currentBackLocation.getBlockY());
        assertEquals(secondBackLocation.getBlockZ(), currentBackLocation.getBlockZ());
    }

    private Location safeLocation(int x, int y, int z) {
        world.getBlockAt(x, y - 1, z).setType(Material.STONE);
        world.getBlockAt(x, y, z).setType(Material.AIR);
        world.getBlockAt(x, y + 1, z).setType(Material.AIR);
        return new Location(world, x, y, z);
    }

    private Location getSavedBackLocation(UUID playerUUID) throws Exception {
        Object backLocation = getBackLocations().get(playerUUID);
        if (backLocation == null) {
            return null;
        }

        Method locationAccessor = backLocation.getClass().getDeclaredMethod("location");
        locationAccessor.setAccessible(true);
        return (Location) locationAccessor.invoke(backLocation);
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, ?> getBackLocations() throws Exception {
        Field field = TeleportManager.class.getDeclaredField("backLocations");
        field.setAccessible(true);
        return (Map<UUID, ?>) field.get(TeleportManager.getInstance());
    }

    private void clearBackLocations() throws Exception {
        getBackLocations().clear();
    }
}
