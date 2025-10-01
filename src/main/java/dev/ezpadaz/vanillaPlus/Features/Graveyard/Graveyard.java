package dev.ezpadaz.vanillaPlus.Features.Graveyard;

import dev.ezpadaz.vanillaPlus.Features.Graveyard.Commands.GraveyardCommand;
import dev.ezpadaz.vanillaPlus.Features.Graveyard.Listeners.GraveyardListener;
import dev.ezpadaz.vanillaPlus.Features.Graveyard.Manager.GraveManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;

public class Graveyard {
    public static void initialize() {
        if (!GeneralHelper.getConfigBool("features.graveyard.enabled")) {
            return;
        }

        GraveManager.loadGravesFromFile();
        GeneralHelper.registerListener(new GraveyardListener());

        if (GeneralHelper.getConfigBool("features.graveyard.enable-grave-deletion")) {
            GraveManager.startGraveyardDeletionTask();
        }

        GeneralHelper.registerCommand(new GraveyardCommand());
    }

    public static void shutDown() {
        GraveManager.saveGravesToFile();
        GraveManager.stopGraveyardDeletionTask();
    }
}
