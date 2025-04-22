package dev.ezpadaz.vanillaPlus.Features.Commands.Admin.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class AdminInventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().title().toString().contains("HyperVisor")) {
            event.setCancelled(true);
        }
    }
}
