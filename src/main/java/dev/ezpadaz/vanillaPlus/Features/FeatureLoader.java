package dev.ezpadaz.vanillaPlus.Features;

import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;

public class FeatureLoader {
    public static void loadAll() {
        if (GeneralHelper.getConfigBool("features.double-xp.enabled")) DoubleXP.initialize();
    }
}
