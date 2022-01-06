package net.tinetwork.tradingcards.api.addons;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author sarhatabaot
 */
public class AddonConfigFile {
    private String resourcePath = "";
    protected final TradingCardsAddon addon;
    protected final String fileName;
    protected final File folder;

    protected File file;
    protected FileConfiguration config;

    public AddonConfigFile(final @NotNull TradingCardsAddon addon, final String resourcePath, final String fileName, final String folder) {
        this.addon = addon;
        this.fileName = fileName;
        this.resourcePath = resourcePath;
        this.folder = new File(addon.getJavaPlugin().getDataFolder().getPath() + File.separator + folder);
    }

    public AddonConfigFile(final @NotNull TradingCardsAddon addon, final String fileName) {
        this.addon = addon;
        this.fileName = fileName;
        this.folder = addon.getJavaPlugin().getDataFolder();
    }

    public void saveDefaultConfig() {
        if (this.file == null) {
            this.file = new File(folder, fileName);
        }

        if (!this.file.exists()) {
            addon.getJavaPlugin().saveResource(resourcePath + fileName, false);
        }

        reloadConfig();
    }

    public void saveConfig() {
        if (this.config == null)
            return;

        if (file == null)
            return;

        try {
            config.save(file);
        } catch (IOException ex) {
            addon.getJavaPlugin().getLogger().warning(ex.getMessage());
        }
    }


    public void reloadConfig() {
        if (file == null) {
            file = new File(folder, fileName);
        }

        config = YamlConfiguration.loadConfiguration(file);
    }

    public void reloadDefaultConfig() {
        if (file == null) {
            file = new File(folder, fileName);
        }

        if (!file.exists()) {
            config = YamlConfiguration.loadConfiguration(file);
            Reader defConfigStream;
            defConfigStream = new InputStreamReader(addon.getJavaPlugin().getResource(resourcePath + fileName), StandardCharsets.UTF_8);
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                config.setDefaults(defConfig);
            }
        }
    }

    @NotNull
    public FileConfiguration getConfig() {
        if (config == null) {
            reloadConfig();
        }
        return this.config;
    }
}