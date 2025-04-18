package dev.ezpadaz.vanillaPlus.Features.Debug;

import dev.ezpadaz.vanillaPlus.Features.Debug.Commands.DebugCommand;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;

public class Debug {
    public static void initialize() {
        if (!GeneralHelper.getConfigBool("debugMode")) return;
        GeneralHelper.registerCommand(new DebugCommand());

        MessageHelper.console("&6DebugMode &a[ON]");
    }
}
