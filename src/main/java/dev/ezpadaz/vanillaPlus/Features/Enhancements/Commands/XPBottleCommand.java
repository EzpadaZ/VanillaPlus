package dev.ezpadaz.vanillaPlus.Features.Enhancements.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Utils.ExperienceHelper;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.UUID;

@CommandAlias("bottle|xpbottle|xpb")
public class XPBottleCommand extends BaseCommand {
    private static final NamespacedKey XP_KEY = new NamespacedKey(VanillaPlus.getInstance(), "experience");
    private static final NamespacedKey UUID_KEY = new NamespacedKey(VanillaPlus.getInstance(), "uuid");


    @Subcommand("store|guardar|save")
    public void onStoreCommand(Player p) {
        ItemStack mainHand = p.getInventory().getItemInMainHand();

        if (mainHand.getType().equals(Material.GLASS_BOTTLE)) {

            // Find available slot, if main hand is 1 glass bottle use that if not, get the first empty slot.
            // This will be -1 if empty.
            int inventorySlot = mainHand.getAmount() == 1 ? p.getInventory().getHeldItemSlot() : p.getInventory().firstEmpty();

            int experience = ExperienceHelper.getPlayerExp(p);
            int levels = p.getLevel();

            if(experience < GeneralHelper.getConfigInt("features.enhancements.xp-storage.min-xp")){
                MessageHelper.send(p, GeneralHelper.getLangString("features.xp-storage.cmd-not-enough-xp"));
                return;
            }

            ItemStack bottle = generateBottle(experience, levels);

            mainHand.setAmount(mainHand.getAmount() - 1);
            p.getInventory().setItemInMainHand(mainHand.getAmount() <= 0 ? new ItemStack(Material.AIR) : mainHand);

            if (inventorySlot == -1) {
                // throw bottle on ground.
                p.getWorld().dropItemNaturally(p.getLocation(), bottle);
                return;
            }

            p.getInventory().setItem(inventorySlot, bottle);
            ExperienceHelper.changePlayerExp(p, -experience);
            MessageHelper.send(p,GeneralHelper.getLangString("features.xp-storage.cmd-store-success").replace("%total_xp%", Integer.toString(experience)));
        } else {
            // send the player that he needs a bottle.
            MessageHelper.send(p, GeneralHelper.getLangString("features.xp-storage.cmd-bottle-needed"));
        }
    }

    public ItemStack generateBottle(int experience, int levels) {
        ItemStack xpbottle = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta meta = xpbottle.getItemMeta();

        Component title = MessageHelper.getMessageComponent(GeneralHelper.getLangString("features.xp-storage.bottle-name"));

        meta.displayName(title);

        Component totalXP = MessageHelper.getMessageComponent(GeneralHelper.getLangString("features.xp-storage.bottle-total").replace("%total_xp%", Integer.toString(experience)));
        Component totalLevels = MessageHelper.getMessageComponent(GeneralHelper.getLangString("features.xp-storage.bottle-levels").replace("%total_levels%", Integer.toString(levels)));

        List<Component> lore = List.of(
                totalXP,
                totalLevels);

        // set item meta to total xp and levels if possible.
        meta.lore(lore);
        PersistentDataContainer container = meta.getPersistentDataContainer();

        container.set(XP_KEY, PersistentDataType.INTEGER, experience);
        container.set(UUID_KEY, PersistentDataType.STRING, UUID.randomUUID().toString()); // this prevents stacking.

        xpbottle.setItemMeta(meta);
        return xpbottle;
    }
}
