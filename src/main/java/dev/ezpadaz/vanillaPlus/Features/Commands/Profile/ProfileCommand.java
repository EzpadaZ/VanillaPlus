package dev.ezpadaz.vanillaPlus.Features.Commands.Profile;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("perfil|profile")
@Description("Ve la informacion de perfil de un jugador.")
public class ProfileCommand extends BaseCommand {

    @Default
    public void onProfileCommand(Player player) {
        List<String> mensajes = new ArrayList<>();
        mensajes.add("&7Nombre: &b" + player.getName());
        mensajes.add("&7Tiempo en esta sesión: &a" + ProfileHelper.getPlaytime(player));
        mensajes.add("&7Salud: &c" + ProfileHelper.getHealth(player) + " &7/&c " + ProfileHelper.getMaxHealth(player));
        mensajes.add("&7Mana: &9" + ProfileHelper.getMana(player));
        mensajes.add("&7Hambre: &6" + ProfileHelper.getHunger(player));
        mensajes.add("&7Saturación: &e" + ProfileHelper.getFoodSaturation(player));
        MessageHelper.sendMultipleMessage(player, "&b&lDatos de Perfil", "INFO", mensajes);
    }

    @Subcommand("ver|see")
    @CommandCompletion("@players")
    public void onProfileOther(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            MessageHelper.send(sender, "&cEl jugador no existe o no esta conectado.");
            return;
        }

        List<String> mensajes = new ArrayList<>();
        mensajes.add("&7Nombre: &b" + target.getName());
        mensajes.add("&7Tiempo en esta sesión: &a" + ProfileHelper.getPlaytime(target));
        mensajes.add("&7Salud: &c" + ProfileHelper.getHealth(target) + " &7/&c" + ProfileHelper.getMaxHealth(target));
        mensajes.add("&7Mana: &9" + ProfileHelper.getMana(target));
        mensajes.add("&7Hambre: &6" + ProfileHelper.getHunger(target));
        mensajes.add("&7Saturación: &e" + ProfileHelper.getFoodSaturation(target));
        MessageHelper.sendMultipleMessage(sender, "&b&lDatos de " + target.getName(), "INFO", mensajes);
    }
}
