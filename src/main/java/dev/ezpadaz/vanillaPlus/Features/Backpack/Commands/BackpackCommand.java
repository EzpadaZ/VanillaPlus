package dev.ezpadaz.vanillaPlus.Features.Backpack.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Features.Backpack.Utils.BackpackManager;
import org.bukkit.entity.Player;

@CommandAlias("backpack")
@CommandPermission("vanillaplus.backpack")
public class BackpackCommand extends BaseCommand {
    @Subcommand("open")
    @Description("Opens your backpack")
    public void onOpenCommand(Player sender) {
        BackpackManager.openBackpack(sender);
    }
}
