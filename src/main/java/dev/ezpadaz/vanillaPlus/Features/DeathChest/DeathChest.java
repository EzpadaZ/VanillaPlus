package dev.ezpadaz.vanillaPlus.Features.DeathChest;

import dev.ezpadaz.vanillaPlus.Features.DeathChest.Listeners.DeathChestListener;
import dev.ezpadaz.vanillaPlus.Features.DeathChest.Manager.DeathManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;

public class DeathChest {
    public static void initialize() {
        if(!GeneralHelper.getConfigBool("features.death-chest.enabled")){
            //MessageHelper.console("Teleport is disabled.");
            return;
        }

        DeathManager.loadGravesFromFile();
        GeneralHelper.registerListener(new DeathChestListener());
    }

    public static void shutDown() {
        DeathManager.saveGravesToFile();
    }
}
