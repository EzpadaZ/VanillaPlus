package dev.ezpadaz.vanillaPlus.Features.Debug.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Utils.EffectHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("vpdebug|vpd")
@Description("Test Commands")
public class DebugCommand extends BaseCommand {
    @Subcommand("te smoke")
    @Description("Test Effects On You")
    public void onTestSmokeEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().smokeEffect(target, 3);
    }

    @Subcommand("te arc")
    @Description("Test Effects On You")
    public void onTestArcEffectCommand(CommandSender sender) {
        Player target = (Player) sender;
        EffectHelper.getInstance().arcEffect(target, 3);
    }
}
