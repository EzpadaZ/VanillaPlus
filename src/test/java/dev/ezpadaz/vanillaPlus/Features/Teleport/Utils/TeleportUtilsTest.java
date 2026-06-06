package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TeleportUtilsTest {
    private ServerMock server;
    private World world;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        world = server.addSimpleWorld("world");
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void isSafeRejectsNullLocation() {
        assertFalse(TeleportUtils.isSafe(null));
    }

    @Test
    void isSafeRejectsNullWorld() {
        assertFalse(TeleportUtils.isSafe(new Location(null, 0, 64, 0)));
    }

    @Test
    void isSafeAcceptsSolidFootingAndClearSpace() {
        makeSafeColumn(0, 64, 0);

        assertTrue(TeleportUtils.isSafe(new Location(world, 0, 64, 0)));
    }

    @Test
    void isSafeRejectsMissingFooting() {
        makeSafeColumn(0, 64, 0);
        world.getBlockAt(0, 63, 0).setType(Material.AIR);

        assertFalse(TeleportUtils.isSafe(new Location(world, 0, 64, 0)));
    }

    @Test
    void isSafeRejectsBlockedHeadSpace() {
        makeSafeColumn(0, 64, 0);
        world.getBlockAt(0, 65, 0).setType(Material.STONE);

        assertFalse(TeleportUtils.isSafe(new Location(world, 0, 64, 0)));
    }

    @ParameterizedTest
    @EnumSource(value = Material.class, names = {
            "LAVA",
            "FIRE",
            "SOUL_FIRE",
            "MAGMA_BLOCK",
            "CACTUS",
            "CAMPFIRE",
            "SOUL_CAMPFIRE",
            "SWEET_BERRY_BUSH",
            "POWDER_SNOW"
    })
    void isSafeRejectsHazards(Material hazard) {
        makeSafeColumn(0, 64, 0);
        world.getBlockAt(0, 64, 0).setType(hazard);

        assertFalse(TeleportUtils.isSafe(new Location(world, 0, 64, 0)));
    }

    private void makeSafeColumn(int x, int y, int z) {
        world.getBlockAt(x, y - 1, z).setType(Material.STONE);
        world.getBlockAt(x, y, z).setType(Material.AIR);
        world.getBlockAt(x, y + 1, z).setType(Material.AIR);
    }
}
