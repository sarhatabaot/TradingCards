package net.tinetwork.tradingcards.api.addons;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * @author sarhatabaot
 */
public class AddonConfigFile {
    private final String internalFilePath;
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
        this.internalFilePath = resourcePath + fileName;
    }

    public AddonConfigFile(final @NotNull TradingCardsAddon addon, final String fileName) {
        this.addon = addon;
        this.fileName = fileName;
        this.folder = addon.getJavaPlugin().getDataFolder();
        this.internalFilePath = resourcePath + fileName;
    }

    public void saveDefaultConfig() {
        if (this.file == null) {
            this.file = new File(folder, fileName);
        }

        if (!this.file.exists()) {
            addon.getJavaPlugin().saveResource(internalFilePath, false);
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
            addon.getAddonLogger().severe("",ex);
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
            try (InputStream internalResourceStream = addon.getJavaPlugin().getResource(internalFilePath)) {
                if (internalResourceStream == null) {
                    throw new NullPointerException("Could not get " + internalFilePath + "for some reason. Possibly missing the file.");
                }

                try (Reader defConfigStream = new InputStreamReader(internalResourceStream, StandardCharsets.UTF_8)) {
                    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                    config.setDefaults(defConfig);

                }
            } catch (IOException | NullPointerException e) {
                addon.getAddonLogger().severe("", e);
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