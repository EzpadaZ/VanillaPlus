package dev.ezpadaz.vanillaPlus;

import co.aikar.commands.PaperCommandManager;
import dev.ezpadaz.vanillaPlus.Features.FeatureLoader;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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

        MessageHelper.console("&6Command Manager: &a[OK]");
        MessageHelper.console("&6Running on: &c" + getServer().getVersion());
        MessageHelper.console("&6Plugin Version: &c" + getDescription().getVersion());
        MessageHelper.console("&6Plugin Status: &a[OK]");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


}
