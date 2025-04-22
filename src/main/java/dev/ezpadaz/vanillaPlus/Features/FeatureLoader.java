package dev.ezpadaz.vanillaPlus.Features;

import dev.ezpadaz.vanillaPlus.Features.Arbiter.Arbiter;
import dev.ezpadaz.vanillaPlus.Features.Commands.Admin.Admin;
import dev.ezpadaz.vanillaPlus.Features.Commands.Miscellaneous;
import dev.ezpadaz.vanillaPlus.Features.Enhancements.GameplayEnhancer;
import dev.ezpadaz.vanillaPlus.Features.Graveyard.Graveyard;
import dev.ezpadaz.vanillaPlus.Features.Debug.Debug;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
import dev.ezpadaz.vanillaPlus.Features.Homes.Homes;
import dev.ezpadaz.vanillaPlus.Features.Teleport.Teleport;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import org.bukkit.Registry;

import java.util.Objects;

public class FeatureLoader {
    public static void loadAll() {
        loadCompletions();
        DoubleXP.initialize();
        Teleport.initialize();
        Debug.initialize();
        Arbiter.initialize();
        Graveyard.initialize();
        Homes.initialize();
        GameplayEnhancer.initialize();
        Miscellaneous.initialize();
        Admin.initialize();
    }

    public static void loadCompletions() {
        VanillaPlus.getInstance().commandManager.getCommandCompletions().registerCompletion("sounds", c ->
                Registry.SOUNDS.stream()
                        .map(sound -> Objects.requireNonNull(Registry.SOUNDS.getKey(sound)).toString())
                        .toList()
        );
    }

    public static void shutdownAll() {
        // Call death chest save.
        Teleport.shutDown();
        Graveyard.shutDown();
        Homes.shutDown();
        Arbiter.shutDown();
    }
}
