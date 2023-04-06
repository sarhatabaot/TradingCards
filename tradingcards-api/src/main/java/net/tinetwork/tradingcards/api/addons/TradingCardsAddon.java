package net.tinetwork.tradingcards.api.addons;


import com.lapzupi.dev.config.HoconConfigurateFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

/**
 * @author sarhatabaot
 */
public interface TradingCardsAddon {
	/**
	 * Return an instance of your plugin.
	 * @return The instance of your {@link JavaPlugin}
	 */
	@NotNull
	JavaPlugin getJavaPlugin();

	/**
	 * @return the addon logger.
	 */
	default Logger getAddonLogger() {
		return getJavaPlugin().getLogger();
	}

	/**
	 * @return the addons config file
	 */
	HoconConfigurateFile<JavaPlugin> getConfig();


	/**
	 * This will execute when we reload the TradingCards main plugin.
	 * By default, we reload just the config.
	 */
	default void onReload() {
		getConfig().reloadConfig();
	}

}
