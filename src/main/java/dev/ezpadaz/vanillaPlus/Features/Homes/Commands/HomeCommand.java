package dev.ezpadaz.vanillaPlus.Features.Homes.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import dev.ezpadaz.vanillaPlus.Features.Homes.Manager.HomeManager;
import org.bukkit.entity.Player;

@CommandAlias("home|casa")
@Description("Comando para configurar y viajar a ubicaciones")
public class HomeCommand extends BaseCommand {

    @Subcommand("add|create|make|crear")
    @Description("Guarda una ubicacion como casa.")
    public void onHomeCreate(Player player, String homeName) {
        HomeManager.addHome(player, homeName);
    }

    @Subcommand("travel|t|viajar")
    @CommandCompletion("@player_homes")
    @Description("Guarda una ubicacion como casa.")
    public void onHomeTravel(Player player, String homeName) {
        HomeManager.travelHome(player, homeName);
    }

    @Subcommand("delete|erase|borrar")
    @CommandCompletion("@player_homes")
    @Description("Borra una ubicacion como casa.")
    public void onHomeDelete(Player player, String homeName) {
        HomeManager.deleteHome(player, homeName);
    }
}
