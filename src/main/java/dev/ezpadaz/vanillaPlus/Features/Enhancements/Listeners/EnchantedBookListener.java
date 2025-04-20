package dev.ezpadaz.vanillaPlus.Features.Enhancements.Listeners;

import dev.ezpadaz.vanillaPlus.Features.Enhancements.GameplayEnhancer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EnchantedBookListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        GameplayEnhancer.saveEnchantedBook(event);
    }
}
