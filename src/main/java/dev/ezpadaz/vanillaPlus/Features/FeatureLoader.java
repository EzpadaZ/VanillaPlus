package dev.ezpadaz.vanillaPlus.Features;

import dev.ezpadaz.vanillaPlus.Features.Debug.Debug;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Features.Teleport.Teleport;
import dev.ezpadaz.vanillaPlus.Utils.GeneralHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Registry;

import java.util.Objects;
import java.util.stream.StreamSupport;

public class FeatureLoader {
    public static void loadAll() {
        loadCompletions();
        DoubleXP.initialize();
        Teleport.initialize();
        Debug.initialize();
    }

    public static void loadCompletions() {
        VanillaPlus.getInstance().commandManager.getCommandCompletions().registerCompletion("sounds", c ->
                Registry.SOUNDS.stream()
                        .map(sound -> Objects.requireNonNull(Registry.SOUNDS.getKey(sound)).toString())
                        .toList()
        );
    }
}
