package dev.ezpadaz.vanillaPlus.Features.Enhancements;

import dev.ezpadaz.vanillaPlus.Features.Enhancements.Commands.XPBottleCommand;
import dev.ezpadaz.vanillaPlus.Features.Enhancements.Listeners.EnchantedBookListener;
import dev.ezpadaz.vanillaPlus.Features.Enhancements.Listeners.XPBottleListener;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.InventoryHelper;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.ChiseledBookshelf;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ChiseledBookshelfInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GameplayEnhancer {
    public static void initialize() {
        boolean allowAllBooksInBookshelf = GeneralHelper.getConfigBool("features.enhancements.allow-any-books-in-bookshelf");
        boolean allowXPStorage = GeneralHelper.getConfigBool("features.enhancements.xp-storage.enabled");

        if (allowAllBooksInBookshelf) GeneralHelper.registerListener(new EnchantedBookListener());

        if (allowXPStorage) {
            // register listener, and command.
            GeneralHelper.registerListener(new XPBottleListener());
            GeneralHelper.registerCommand(new XPBottleCommand());
        }
    }
}
