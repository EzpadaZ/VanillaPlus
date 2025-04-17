package dev.ezpadaz.vanillaPlus.Features.Teleport.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("teleport|tp|viaje")
public class TeleportCommand extends BaseCommand {

    @Subcommand("here|traer|aqui")
    @Description("Trae a alguien a tu ubicación")
    @CommandCompletion("@players")
    public void onTeleportHereCommand(Player sender, Player target) {

    }

    @Subcommand("to|ir|hacia")
    @Description("Ve con el jugador objetivo")
    @CommandCompletion("@players")
    public void onTeleportTo(Player sender, Player target) {

    }

    @Subcommand("back|regresar|atras")
    @Description("Regresa a la ubicacion anterior a tu viaje")
    public void onTeleportBack(Player sender) {

    }

    @Subcommand("accept|aceptar|a")
    @Description("Acepta alguna peticion pendiente")
    public void onTeleportAcceptCommand(Player sender) {

    }

    @Subcommand("cancel|cancelar|c")
    @Description("Cancela alguna peticion pendiente")
    public void onTeleportCancelCommand(Player sender) {

    }
}
