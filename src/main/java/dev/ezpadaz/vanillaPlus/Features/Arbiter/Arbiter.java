package dev.ezpadaz.vanillaPlus.Features.Arbiter;

import dev.ezpadaz.vanillaPlus.Features.Arbiter.Commands.ControlCommand;
import dev.ezpadaz.vanillaPlus.Features.Arbiter.Core.Watcher;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;

public class Arbiter {
    public static void initialize() {
        if (!GeneralHelper.getConfigBool("features.watcher.enabled")) return;

        GeneralHelper.registerCommand(new ControlCommand());

        Watcher.getInstance().initialize();
        Watcher.getInstance().startProtection();
        MessageHelper.console("&6Arbiter &a[OK]");
    }
}
