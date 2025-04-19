package dev.ezpadaz.vanillaPlus.Features.Graveyard.Listeners;

import dev.ezpadaz.vanillaPlus.Features.Graveyard.Manager.DeathManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class GraveyardListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // Set event for death saving chest.
        Player player = e.getEntity();

        // Prevent item and XP drops
        e.getDrops().clear();
        e.setDroppedExp(0);
        DeathManager.spawnGrave(e.getEntity());

        if (GeneralHelper.getConfigBool("features.graveyard.instant-respawn")) {
            // Instant respawn is enabled.
            Bukkit.getScheduler().runTaskLater(VanillaPlus.getInstance(), () -> {
                player.spigot().respawn();
                GeneralHelper.playSound("particle.soul_escape", player.getLocation(), 10.0f, 0.4f);
            }, 1L);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block clicked = event.getClickedBlock();
        if (clicked == null || clicked.getType() != Material.PLAYER_HEAD) return;

        if (!DeathManager.isGrave(clicked.getLocation())) return;

        event.setCancelled(true);
        DeathManager.sendGraveInfo(event.getPlayer(), clicked.getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.PLAYER_HEAD) {
            if (DeathManager.isGrave(block.getLocation())) {
                event.setCancelled(true);
                DeathManager.restoreGrave(event.getPlayer(), block.getLocation());
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
                DeathManager.isGrave(block.getLocation());
    }
}
