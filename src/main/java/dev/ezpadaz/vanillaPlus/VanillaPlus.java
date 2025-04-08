package dev.ezpadaz.vanillaPlus;

import co.aikar.commands.PaperCommandManager;
import dev.ezpadaz.vanillaPlus.Features.FeatureLoader;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class VanillaPlus extends JavaPlugin {
    private static VanillaPlus instance;
    public FileConfiguration config = getConfig();

    public PaperCommandManager commandManager;

    public static VanillaPlus getInstance() { return instance; }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        commandManager = new PaperCommandManager(this);

        FeatureLoader.loadAll();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
