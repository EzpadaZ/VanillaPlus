package dev.ezpadaz.vanillaPlus.Features.Teleport.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.ezpadaz.vanillaPlus.Features.Teleport.Utils.TeleportManager;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("teleport|tp|viaje")
public class TeleportCommand extends BaseCommand {

    @Subcommand("here|traer|aqui")
    @Description("Trae a alguien a tu ubicación")
    @CommandCompletion("@players")
    public void onTeleportHereCommand(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        TeleportManager.getInstance().sendRequest(sender, target, true);
    }

    @Subcommand("to|ir|hacia")
    @Description("Ve con el jugador objetivo")
    @CommandCompletion("@players")
    public void onTeleportTo(Player sender, String[] args) {
        Player target = Bukkit.getPlayer(args[0]);
        TeleportManager.getInstance().sendRequest(sender, target, false);
    }

    @Subcommand("back|regresar|atras")
    @Description("Regresa a la ubicacion anterior a tu viaje")
    public void onTeleportBack(Player sender) {
        TeleportManager.getInstance().teleportBack(sender);
    }

    @Subcommand("accept|aceptar|a")
    @Description("Acepta alguna peticion pendiente")
    public void onTeleportAcceptCommand(Player sender, String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            MessageHelper.send(sender, "&cPor favor acepta haciendo click en los botones en el chat.");
            return;
        }

        TeleportManager.getInstance().acceptRequest(sender, args[0]);
    }

    @Subcommand("cancel|cancelar|c")
    @Description("Cancela alguna peticion pendiente")
    public void onTeleportCancelCommand(Player sender, String[] args) {
        if (args.length == 0 || args[0].isEmpty()) {
            MessageHelper.send(sender, "&cPor favor acepta haciendo click en los botones en el chat.");
            return;
        }

        TeleportManager.getInstance().cancelRequest(sender, args[0]);
    }
}
