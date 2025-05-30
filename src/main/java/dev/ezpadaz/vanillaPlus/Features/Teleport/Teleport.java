package dev.ezpadaz.vanillaPlus.Features.Teleport;

import dev.ezpadaz.vanillaPlus.Features.Teleport.Commands.TeleportCommand;
import dev.ezpadaz.vanillaPlus.Features.Teleport.Utils.TeleportManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;

public class Teleport {
    public static void initialize() {
        if(!GeneralHelper.getConfigBool("features.teleport.enabled")){
            //MessageHelper.console("Teleport is disabled.");
            return;
        }

        // Register Commands
        GeneralHelper.registerCommand(new TeleportCommand());
        TeleportManager.getInstance().initialize();
        MessageHelper.console("&6Teleport &a[OK]");
    }

    public static void shutDown() {
        // Clear TP Queue.
        TeleportManager.getInstance().clearQueue();
    }
}
