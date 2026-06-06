package dev.ezpadaz.vanillaPlus.Features.Homes.Manager;

import dev.ezpadaz.vanillaPlus.Features.Homes.Utils.HomeData;
import dev.ezpadaz.vanillaPlus.Features.Homes.Utils.SerializableLocation;
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

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HomeManagerTest {
    private ServerMock server;
    private World world;
    private VanillaPlus plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(VanillaPlus.class);
        world = server.addSimpleWorld("world");
        HomeManager.clearHomesForTesting();
        HomeManager.MAX_HOMES = 2;
        HomeManager.TELEPORT_DELAY = 0;
    }

    @AfterEach
    void tearDown() {
        HomeManager.clearHomesForTesting();
        MockBukkit.unmock();
    }

    @Test
    void addHomeStoresTrimmedName() {
        PlayerMock player = server.addPlayer("Molthor226");
        player.teleport(safeLocation(0, 64, 0));

        HomeManager.addHome(player, " base ");

        assertEquals(List.of("base"), HomeManager.getHomeNames(player));
    }

    @Test
    void addHomeWithSameNameUpdatesExistingHomeWithoutIncreasingCount() {
        PlayerMock player = server.addPlayer("Molthor226");
        player.teleport(safeLocation(0, 64, 0));
        HomeManager.addHome(player, "base");

        player.teleport(safeLocation(10, 70, 0));
        HomeManager.addHome(player, "BASE");

        assertEquals(1, HomeManager.getHomeNames(player).size());

        player.teleport(safeLocation(50, 80, 0));
        HomeManager.travelHome(player, "base");
        server.getScheduler().performOneTick();

        assertEquals(10, player.getLocation().getBlockX());
        assertEquals(70, player.getLocation().getBlockY());
    }

    @Test
    void addHomeEnforcesMaxHomes() {
        PlayerMock player = server.addPlayer("Molthor226");

        player.teleport(safeLocation(0, 64, 0));
        HomeManager.addHome(player, "one");
        player.teleport(safeLocation(10, 64, 0));
        HomeManager.addHome(player, "two");
        player.teleport(safeLocation(20, 64, 0));
        HomeManager.addHome(player, "three");

        assertEquals(List.of("one", "two"), HomeManager.getHomeNames(player));
    }

    @Test
    void deleteHomeRemovesExistingHome() {
        PlayerMock player = server.addPlayer("Molthor226");
        player.teleport(safeLocation(0, 64, 0));
        HomeManager.addHome(player, "base");

        HomeManager.deleteHome(player, "base");

        assertTrue(HomeManager.getHomeNames(player).isEmpty());
    }

    @Test
    void deleteMissingHomeLeavesExistingHomes() {
        PlayerMock player = server.addPlayer("Molthor226");
        player.teleport(safeLocation(0, 64, 0));
        HomeManager.addHome(player, "base");

        HomeManager.deleteHome(player, "mine");

        assertEquals(List.of("base"), HomeManager.getHomeNames(player));
    }

    @Test
    void loadHomesClearsStaleInMemoryHomes() throws Exception {
        PlayerMock player = server.addPlayer("Molthor226");
        HomeManager.putHomeForTesting(player.getUniqueId(), new HomeData(player.getUniqueId(), "stale", new SerializableLocation(safeLocation(0, 64, 0))));

        File file = new File(plugin.getDataFolder(), "data/homes/homes.json");
        file.getParentFile().mkdirs();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("{}");
        }

        HomeManager.loadHomesFromFile();

        assertTrue(HomeManager.getHomeNames(player).isEmpty());
    }

    @Test
    void travelHomeWithMissingWorldDoesNotTeleport() {
        PlayerMock player = server.addPlayer("Molthor226");
        Location start = safeLocation(0, 64, 0);
        player.teleport(start);
        HomeManager.putHomeForTesting(player.getUniqueId(), new HomeData(player.getUniqueId(), "lost", missingWorldLocation("missing_world")));

        HomeManager.travelHome(player, "lost");
        server.getScheduler().performTicks(20L);

        assertEquals(start.getBlockX(), player.getLocation().getBlockX());
        assertEquals(start.getBlockY(), player.getLocation().getBlockY());
        assertEquals(start.getBlockZ(), player.getLocation().getBlockZ());
    }

    @Test
    void adminTeleportWithMissingWorldDoesNotTeleport() {
        PlayerMock owner = server.addPlayer("Molthor226");
        PlayerMock target = server.addPlayer("Target");
        Location start = safeLocation(0, 64, 0);
        owner.teleport(start);
        HomeManager.putHomeForTesting(target.getUniqueId(), new HomeData(target.getUniqueId(), "lost", missingWorldLocation("missing_world")));

        HomeManager.adminTeleportToUserHome(owner, "lost/Target");
        server.getScheduler().performTicks(20L);

        assertEquals(start.getBlockX(), owner.getLocation().getBlockX());
        assertEquals(start.getBlockY(), owner.getLocation().getBlockY());
        assertEquals(start.getBlockZ(), owner.getLocation().getBlockZ());
    }

    @Test
    void slashIsRejectedInHomeNames() {
        PlayerMock player = server.addPlayer("Molthor226");

        HomeManager.addHome(player, "bad/name");

        assertFalse(HomeManager.getHomeNames(player).contains("bad/name"));
    }

    private Location safeLocation(int x, int y, int z) {
        world.getBlockAt(x, y - 1, z).setType(Material.STONE);
        world.getBlockAt(x, y, z).setType(Material.AIR);
        world.getBlockAt(x, y + 1, z).setType(Material.AIR);
        return new Location(world, x, y, z);
    }

    private SerializableLocation missingWorldLocation(String worldName) {
        SerializableLocation location = new SerializableLocation();
        location.world = worldName;
        location.x = 10;
        location.y = 70;
        location.z = 10;
        location.yaw = 0;
        location.pitch = 0;
        return location;
    }
}
