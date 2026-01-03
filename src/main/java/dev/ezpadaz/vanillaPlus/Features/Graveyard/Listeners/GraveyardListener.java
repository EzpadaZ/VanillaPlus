package dev.ezpadaz.vanillaPlus.Features.Graveyard.Listeners;

import dev.ezpadaz.vanillaPlus.Features.Graveyard.Manager.GraveManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.Utils.SchedulerHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class GraveyardListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();

        // Skip grave in the End
        if (player.getWorld().getEnvironment() != World.Environment.THE_END) {
            // Prevent item and XP drops
            e.getDrops().clear();
            e.setDroppedExp(0);
            GraveManager.spawnGrave(player);

            if (GeneralHelper.getConfigBool("features.graveyard.instant-respawn")) {
                Bukkit.getScheduler().runTaskLater(VanillaPlus.getInstance(), () -> {
                    player.spigot().respawn();
                    player.setInvulnerable(true);
                    player.setGlowing(true);
                    SchedulerHelper.scheduleTask(null, () -> {
                        player.setInvulnerable(false);
                        player.setGlowing(false);
                    }, 2);
                    GeneralHelper.playSound("particle.soul_escape", player.getLocation(), 10.0f, 0.4f);
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null || clicked.getType() != Material.PLAYER_HEAD && clicked.getType() != Material.SKELETON_SKULL) return;

        if (!GraveManager.isGrave(clicked.getLocation())) return;

        event.setCancelled(true);
        GraveManager.sendGraveInfo(event.getPlayer(), clicked.getLocation());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World.Environment environment = player.getWorld().getEnvironment();

        if (environment == World.Environment.THE_END) {
            MessageHelper.send(player, "&cLas tumbas no se generan en El End.");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.PLAYER_HEAD ||  block.getType() == Material.SKELETON_SKULL) {
            if (GraveManager.isGrave(block.getLocation())) {
                event.setCancelled(true);
                GraveManager.restoreGrave(event.getPlayer(), block.getLocation());
            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(this::isProtectedGrave);
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(this::isProtectedGrave);
    }

    private boolean isProtectedGrave(Block block) {
        return block.getType() == Material.PLAYER_HEAD &&
                GraveManager.isGrave(block.getLocation());
    }
}
