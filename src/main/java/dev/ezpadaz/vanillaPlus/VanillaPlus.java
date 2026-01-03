package dev.ezpadaz.vanillaPlus;

import co.aikar.commands.PaperCommandManager;
import dev.ezpadaz.vanillaPlus.Features.FeatureLoader;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Optional;

public final class VanillaPlus extends JavaPlugin {
    private static VanillaPlus instance;
    public PaperCommandManager commandManager;

    public static VanillaPlus getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveOrUpdateLang();
        saveOrUpdateConfig();
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

    public void saveOrUpdateLang() {
        File langFile = new File(getDataFolder(), "lang.yml");
        if (!langFile.exists()) {
            saveResource("lang.yml", false);
            MessageHelper.console("&6Lang: &aCreated default lang.yml");
            return;
        }

        try (var in = getResource("lang.yml")) {
            if (in == null) throw new IOException("Default lang.yml missing in JAR");

            var currentVersion = new BigDecimal(Optional.ofNullable(
                    YamlConfiguration.loadConfiguration(langFile).getString("version")
            ).orElseThrow());

            var newVersion = new BigDecimal(Optional.ofNullable(
                    YamlConfiguration.loadConfiguration(new InputStreamReader(in)).getString("version")
            ).orElseThrow());

            if (currentVersion.compareTo(newVersion) < 0) {
                saveResource("lang.yml", true);
                MessageHelper.console("&6Lang: &aUpdated lang.yml to v" + newVersion);
            }
        } catch (Exception e) {
            saveResource("lang.yml", true);
            MessageHelper.console("&6Lang: &cVersion check failed, lang.yml overwritten. (" + e.getMessage() + ")");
        }
    }


    public void saveOrUpdateConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
            MessageHelper.console("&6Config: &aCreated default config.yml");
            return;
        }
        try (var in = getResource("config.yml")) {
            if (in == null) throw new IOException("Default config.yml missing in JAR");
            var currentVersion = new BigDecimal(Optional.ofNullable(
                    YamlConfiguration.loadConfiguration(configFile).getString("version")
            ).orElseThrow());
            var newVersion = new BigDecimal(Optional.ofNullable(
                    YamlConfiguration.loadConfiguration(new InputStreamReader(in)).getString("version")
            ).orElseThrow());
            if (currentVersion.compareTo(newVersion) < 0) {
                saveResource("config.yml", true);
                MessageHelper.console("&6Config: &aUpdated config.yml to v" + newVersion);
            }
        } catch (Exception e) {
            saveResource("config.yml", true);
            MessageHelper.console("&6Config: &cVersion check failed, config.yml overwritten. (" + e.getMessage() + ")");
        }
    }
}
