package dev.ezpadaz.vanillaPlus.Features.DeathChest.Listeners;

import dev.ezpadaz.vanillaPlus.Features.DeathChest.Manager.DeathManager;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class DeathChestListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        // Set event for death saving chest.
        Player player = e.getEntity();

        // Prevent item and XP drops
        e.getDrops().clear();
        e.setDroppedExp(0);
        DeathManager.spawnGrave(e.getEntity());
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
