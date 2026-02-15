package dev.ezpadaz.vanillaPlus.Features.Backpack.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.ezpadaz.vanillaPlus.Features.Backpack.Utils.BackpackManager;
import org.bukkit.entity.Player;

@CommandAlias("backpack|mochila|b")
public class BackpackCommand extends BaseCommand {
    @Default
    public void onOpenCommand(Player sender) {
        BackpackManager.openBackpack(sender, false);
    }
}
