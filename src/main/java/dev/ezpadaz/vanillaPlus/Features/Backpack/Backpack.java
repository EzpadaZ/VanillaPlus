package dev.ezpadaz.vanillaPlus.Features.Backpack;

import dev.ezpadaz.vanillaPlus.Features.Backpack.Commands.BackpackCommand;
import dev.ezpadaz.vanillaPlus.Features.Backpack.Utils.BackpackManager;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;

public class Backpack {
    public static void initialize() {
        if (!GeneralHelper.getConfigBool("features.backpack.enabled")) {
            return;
        }

        BackpackManager.loadInventoriesFromFile();
        // Enable backpack system.
        GeneralHelper.registerCommand(new BackpackCommand());
    }

    public static void shutDown() {
        BackpackManager.saveInventoriesToFile();
    }
}
