package dev.ezpadaz.vanillaPlus.Features.Enhancements.Listeners;

import dev.ezpadaz.vanillaPlus.Utils.ExperienceHelper;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class XPBottleListener implements Listener {
    private static final NamespacedKey XP_KEY = new NamespacedKey(VanillaPlus.getInstance(), "experience");

    @EventHandler
    public void onThrow(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.EXPERIENCE_BOTTLE) return;

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        if (!pdc.has(XP_KEY, PersistentDataType.INTEGER)) return;

        Player player = event.getPlayer();

        // If this is OFF_HAND, and MAIN_HAND also has a redeemable bottle, ignore OFF_HAND to prevent double redeem
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            ItemStack main = player.getInventory().getItemInMainHand();
            if (main.getType() == Material.EXPERIENCE_BOTTLE) {
                ItemMeta mm = main.getItemMeta();
                if (mm != null && mm.getPersistentDataContainer().has(XP_KEY, PersistentDataType.INTEGER)) {
                    return;
                }
            }
        }

        event.setCancelled(true);

        int exp = pdc.getOrDefault(XP_KEY, PersistentDataType.INTEGER, 0);
        ExperienceHelper.addPlayerExp(player, exp);

        // consume from the correct hand
        if (event.getHand() == EquipmentSlot.HAND) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }

        MessageHelper.send(player, GeneralHelper.getLangString("features.xp-storage.claimed-message")
                .replace("%total_xp%", Integer.toString(exp)));
    }
}
