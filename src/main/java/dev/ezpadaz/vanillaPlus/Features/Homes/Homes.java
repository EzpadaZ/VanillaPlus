package dev.ezpadaz.vanillaPlus.Features.Homes;

import dev.ezpadaz.vanillaPlus.Features.Homes.Commands.HomeCommand;
import dev.ezpadaz.vanillaPlus.Features.Homes.Manager.HomeManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.entity.Player;

public class Homes {
    public static void initialize() {
        if (!GeneralHelper.getConfigBool("features.homes.enabled")) {
            return;
        }

        HomeManager.loadHomesFromFile();

        VanillaPlus.getInstance().commandManager.getCommandCompletions().registerCompletion("player_homes", c -> {
            Player player = (Player) c.getSender();
            return HomeManager.getHomeNames(player);
        });

        VanillaPlus.getInstance().commandManager.getCommandCompletions().registerCompletion("admin_player_homes", c -> {
            Player player = (Player) c.getSender();
            return HomeManager.getHomeNamesWithPlayer(player);
        });

        GeneralHelper.registerCommand(new HomeCommand());
        HomeManager.MAX_HOMES = GeneralHelper.getConfigInt("features.homes.max-homes");
        HomeManager.TELEPORT_DELAY = GeneralHelper.getConfigInt("features.homes.delay");
    }

    public static void shutDown() {
        HomeManager.saveHomesToFile();
    }
}
