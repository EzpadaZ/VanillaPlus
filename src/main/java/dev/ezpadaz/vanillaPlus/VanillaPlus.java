package dev.ezpadaz.vanillaPlus;

import co.aikar.commands.PaperCommandManager;
import dev.ezpadaz.vanillaPlus.Features.FeatureLoader;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.plugin.java.JavaPlugin;

public final class VanillaPlus extends JavaPlugin {
    private static VanillaPlus instance;
    public PaperCommandManager commandManager;

    public static VanillaPlus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        commandManager = new PaperCommandManager(this);
        FeatureLoader.loadAll();

        MessageHelper.console("&6Command Manager: &a[OK]");
        MessageHelper.console("&6Plugin Status: &a[OK]");
        MessageHelper.console("&6Plugin Version: &c" + getDescription().getVersion());
        MessageHelper.console("&6Running on: &c" + getServer().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        FeatureLoader.shutdownAll();
    }
}
