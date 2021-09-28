package net.tinetwork.tradingcards.tradingcardsplugin.core;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Simple class to initialize, reload & save config files
 */
public class SimpleConfigFile {
	private String resourcePath = "";
	protected final TradingCards plugin;
	protected final String fileName;
	protected final File folder;

	protected File file;
	protected FileConfiguration config;

	public SimpleConfigFile(final TradingCards plugin,final String resourcePath, final String fileName, final String folder) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.resourcePath = resourcePath;
		this.folder = new File(plugin.getDataFolder().getPath()+File.separator+folder);
	}

	public SimpleConfigFile(final TradingCards plugin, final String fileName) {
		this.plugin = plugin;
		this.fileName = fileName;
		this.folder = plugin.getDataFolder();
	}

	public void saveDefaultConfig() {
		if (this.file == null) {
			this.file = new File(folder, fileName);
		}

		if (!this.file.exists()) {
			plugin.saveResource(resourcePath + fileName, false);
		}

		reloadConfig();
	}

	public void saveConfig(){
		if(this.config == null)
			return;

		if(file == null)
			return;

		try {
			config.save(file);
		} catch (IOException ex) {
			plugin.getLogger().warning(ex.getMessage());
		}
	}


	public void reloadConfig(){
		if (file == null) {
			file = new File(folder, fileName);
		}

		config = YamlConfiguration.loadConfiguration(file);
	}

	public void reloadDefaultConfig(){
		if (file == null) {
			file = new File(folder, fileName);
		}

		if(!file.exists()) {
			config = YamlConfiguration.loadConfiguration(file);
			Reader defConfigStream;
			defConfigStream = new InputStreamReader(plugin.getResource(resourcePath + fileName), StandardCharsets.UTF_8);
			if (defConfigStream != null) {
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
				config.setDefaults(defConfig);
			}
		}
	}

	@NotNull
	public FileConfiguration getConfig(){
		if(config==null){
			reloadConfig();
		}
		return this.config;
	}

}
