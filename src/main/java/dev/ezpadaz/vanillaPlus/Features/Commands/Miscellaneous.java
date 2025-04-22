package dev.ezpadaz.vanillaPlus.Features.Commands;

import dev.ezpadaz.vanillaPlus.Features.Commands.Profile.ProfileCommand;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;

public class Miscellaneous {
    public static void initialize() {
        GeneralHelper.registerCommand(new ProfileCommand());
    }
}
