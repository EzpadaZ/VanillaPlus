package dev.ezpadaz.vanillaPlus.Features;

import dev.ezpadaz.vanillaPlus.Features.Debug.Debug;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Features.Teleport.Teleport;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;

public class FeatureLoader {
    public static void loadAll() {
        DoubleXP.initialize();
        Teleport.initialize();
        Debug.initialize();
    }
}
