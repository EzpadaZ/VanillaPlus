package dev.ezpadaz.vanillaPlus.Features.Arbiter;

import dev.ezpadaz.vanillaPlus.Features.Arbiter.Commands.ControlCommand;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;

public class Arbiter {
    private static String arbiterPrefix = "&6[&5Arbiter&6] ";

    public void initialize() {
        if (!GeneralHelper.getConfigBool("features.watcher.enabled")) return;

        GeneralHelper.registerCommand(new ControlCommand());

    }
}
