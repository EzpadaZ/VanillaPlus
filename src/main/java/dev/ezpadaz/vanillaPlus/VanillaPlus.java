package dev.ezpadaz.vanillaPlus;

import co.aikar.commands.PaperCommandManager;
import dev.ezpadaz.vanillaPlus.Features.FeatureLoader;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class VanillaPlus extends JavaPlugin {
    private static VanillaPlus instance;
    public PaperCommandManager commandManager;

    public static VanillaPlus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveLangFile();
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

    private void saveLangFile() {
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false); // false = don't overwrite existing file
            MessageHelper.console("&6Lang File: &aCreated from resources.");
        } else {
            MessageHelper.console("&6Lang File: &aAlready exists.");
        }
    }
}
