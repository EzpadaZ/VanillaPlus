package dev.ezpadaz.vanillaPlus.Features.Graveyard;

import dev.ezpadaz.vanillaPlus.Features.Graveyard.Listeners.GraveyardListener;
import dev.ezpadaz.vanillaPlus.Features.Graveyard.Manager.GraveManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;

public class Graveyard {
    public static void initialize() {
        if(!GeneralHelper.getConfigBool("features.graveyard.enabled")){
            //MessageHelper.console("Teleport is disabled.");
            return;
        }

        GraveManager.loadGravesFromFile();
        GeneralHelper.registerListener(new GraveyardListener());
    }

    public static void shutDown() {
        GraveManager.saveGravesToFile();
    }
}
