package dev.ezpadaz.vanillaPlus.Features.Commands.Admin.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.ezpadaz.vanillaPlus.Features.Backpack.Utils.BackpackManager;
import dev.ezpadaz.vanillaPlus.Features.Homes.Manager.HomeManager;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Arrays;

@CommandAlias("admin")
@CommandPermission("vanillaplus.admin")
public class AdminCommand extends BaseCommand {

    @Subcommand("homes")
    public class HomeSubCommand extends BaseCommand {
        @Subcommand("tp")
        @CommandCompletion("@admin_player_homes")
        public void onHomeTpCommand(Player player, String[] args) {
            try {
                HomeManager.adminTeleportToUserHome(player, args[0]);
            }catch(Exception e) {
                MessageHelper.send(player, "&cFallo el teleport.");
            }
        }
    }

    @Subcommand("inventory")
    public class InventorySubCommand extends BaseCommand {

        @Subcommand("see|peek")
        @Description("See the inventory of said player.")
        @CommandCompletion("@players")
        public void onInventorySeeCommand(Player sender, String[] args) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessageHelper.send(sender, "&cEl jugador es inaccesible.");
                return;
            }

            Inventory targetInventory = target.getInventory();
            int size = targetInventory.getSize();

            Component title = Component.text("Hyper", NamedTextColor.DARK_PURPLE, TextDecoration.BOLD)
                    .append(Component.text("Visor", NamedTextColor.GOLD))
                    .append(Component.text(" :: ", NamedTextColor.DARK_RED))
                    .append(Component.text(target.getName(), NamedTextColor.DARK_GREEN));

            Inventory copy = Bukkit.createInventory(null, 54, title);
            copy.setContents(Arrays.copyOf(targetInventory.getContents(), 54));

            // Open inventory
            sender.openInventory(copy);
        }
    }

    @Subcommand("backpack")
    public class BackpackSubCommand extends BaseCommand {
        @Default
        @CommandCompletion("@players")
        public void onAdminBackpack(Player sender, String[] args) {
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                MessageHelper.send(sender, "&cEl jugador debe estar en linea.");
                return;
            }

            BackpackManager.openBackpack(sender, target);
        }
    }
}
