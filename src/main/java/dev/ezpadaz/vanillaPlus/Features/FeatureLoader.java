package dev.ezpadaz.vanillaPlus.Features;

import dev.ezpadaz.vanillaPlus.Features.Arbiter.Arbiter;
import dev.ezpadaz.vanillaPlus.Features.DeathChest.DeathChest;
import dev.ezpadaz.vanillaPlus.Features.Debug.Debug;
import dev.ezpadaz.vanillaPlus.Features.DoubleXP.DoubleXP;
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
        DeathChest.initialize();
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
    }
}
