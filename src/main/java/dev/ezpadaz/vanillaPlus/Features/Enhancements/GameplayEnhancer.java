package dev.ezpadaz.vanillaPlus.Features.Enhancements;

import dev.ezpadaz.vanillaPlus.Features.Enhancements.Listeners.EnchantedBookListener;
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
        if (GeneralHelper.getConfigBool("features.enhancements.allow-any-books-in-bookshelf")) {
            GeneralHelper.registerListener(new EnchantedBookListener());
        }
    }

    public static void saveEnchantedBook(PlayerInteractEvent event) {
        Block blockEvent = event.getClickedBlock();
        ItemStack item = event.getItem();

        if (blockEvent == null) {
            return;
        }

        if (blockEvent.getType() == Material.CHISELED_BOOKSHELF) {
            ChiseledBookshelf bookshelf = ((ChiseledBookshelf) blockEvent.getState());
            ChiseledBookshelfInventory inventory = bookshelf.getInventory();
            if (item != null && item.getType() == Material.ENCHANTED_BOOK) {
                // is a valid enchantment book, save it in the storage.
                Vector pos = event.getClickedPosition();
                int bslot = bookshelf.getSlot(pos); // gets chiseled_bookshelf looked at position.
                ItemStack tempItem = inventory.getItem(bslot);
                Player jugador = event.getPlayer();

                if (tempItem == null) {
                    inventory.setItem(bslot, item);
                    InventoryHelper.removeSpecificItem(event.getPlayer(), item);
                    jugador.playSound(jugador.getLocation(), Sound.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, 1.0f, 1.0f);
                } else {
                    ItemStack returnedItem = inventory.getItem(bslot);
                    inventory.removeItem(returnedItem);
                    jugador.getInventory().addItem(returnedItem);
                }
            }
        }
    }
}
