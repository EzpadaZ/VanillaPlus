package dev.ezpadaz.vanillaPlus.Features.DoubleXP.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Utils.ExperienceHelper;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

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
                if (p.getName().equals(GeneralHelper.getConfigString("owner"))) {
                    if (DoubleXP.isEventEnabled()) {
                        MessageHelper.console("DoubleXP is already enabled.");
                    } else {
                        DoubleXP.setEventEnabled(true);
                        MessageHelper.send(p, "&6DoubleXP &aenabled");
                    }
                } else {
                    MessageHelper.send(p, "Este comando solo corre por consola.");
                }
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
                if (p.getName().equals(GeneralHelper.getConfigString("owner"))) {
                    if (DoubleXP.isEventEnabled()) {
                        DoubleXP.setEventEnabled(false);
                        MessageHelper.send(sender, "&6DoubleXP is &cdisabled.");
                    }
                } else {
                    MessageHelper.send(p, "Este comando solo corre por consola.");
                }
            }
        }
    }

    @Subcommand("get|ver|g")
    @Description("Obtiene tu experiencia actual")
    public void onGetCommand(Player jugador) {
        int totalExpPoints = ExperienceHelper.getPlayerExp(jugador);
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedExp = formatter.format(totalExpPoints);
        MessageHelper.send(jugador, "Tienes: " + formattedExp + " EXP");
    }

    @Subcommand("optin|verxp")
    @Description("Informa al jugador de cambios de xp")
    public void onOptinCommand(CommandSender sender) {
        if (sender instanceof Player p) {
            if (DoubleXP.isPlayerOptedIn(p.getName())) {
                DoubleXP.optedPlayers.remove(p.getName());
                MessageHelper.send(p, "Dejaras de recibir actualizaciones de XP");
            } else {
                DoubleXP.optedPlayers.add(p.getName());
                MessageHelper.send(p, "Ahora recibiras actualizaciones de XP");
            }
        }
    }
}
