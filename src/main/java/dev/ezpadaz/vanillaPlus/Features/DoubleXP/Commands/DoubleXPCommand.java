package dev.ezpadaz.vanillaPlus.Features.DoubleXP.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

@CommandAlias("dxp|exp")
@Description("Administra el modulo de DoubleXP")
public class DoubleXPCommand extends BaseCommand {

    @Subcommand("enable|e|on")
    @Description("Activa el evento de DoubleXP")
    public void onEnableCommand(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            if (DoubleXP.isEventEnabled()) {
                MessageHelper.console("DoubleXP is already enabled.");
            } else {
                DoubleXP.setEventEnabled(true);
            }
        } else {
            if (sender instanceof Player p) {
                MessageHelper.send(p, "Este comando solo corre por consola.");
            }
        }
    }

    @Subcommand("disable|d|off")
    @Description("Desactiva el evento de DoubleXP")
    public void onDisableCommand(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            if (DoubleXP.isEventEnabled()) {
                DoubleXP.setEventEnabled(false);
                MessageHelper.send(sender, "DoubleXP is now disabled.");
            }
        } else {
            if (sender instanceof Player p) {
                MessageHelper.send(p, "Este comando solo corre por consola.");
            }
        }
    }
}
