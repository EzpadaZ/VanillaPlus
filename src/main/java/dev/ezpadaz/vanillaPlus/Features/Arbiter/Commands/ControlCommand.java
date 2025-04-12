package dev.ezpadaz.vanillaPlus.Features.Arbiter.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;

@CommandAlias("arbiter|arb")
@Description("Arbiter Control")
public class ControlCommand extends BaseCommand {

    @Subcommand("info|i")
    @Description("Gets information about the current watcher status")
    public void onInfoCommand(CommandSender sender) {

    }

    @Subcommand("disable|d")
    @Description("Terminates the watcher, disables safeguards")
    public void onDisableCommand(CommandSender sender) {

    }

    @Subcommand("enable|e")
    @Description("Enables the watcher and turns on the safeguard")
    public void onEnableCommand(CommandSender sender) {

    }
}
