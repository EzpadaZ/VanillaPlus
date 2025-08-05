package dev.ezpadaz.vanillaPlus.Features.Graveyard.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("graveyard|tomb")
@Description("Informacion sobre los cofres astrales")
public class GraveyardCommand extends BaseCommand {

    @Subcommand("get")
    public class GraveyardGetSubcommand extends BaseCommand {

        @Subcommand("latest")
        public void onLatestGraveCommand(Player sender) {
            // get latest grave
        }

        @Subcommand("most-xp")
        public void onMostXpGraveCommand(Player sender) {
            // get grave with most xp.
        }
    }
}
