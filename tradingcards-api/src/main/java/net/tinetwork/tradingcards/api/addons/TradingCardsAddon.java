package net.tinetwork.tradingcards.api.addons;


import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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
	 *
	 * @return the addon logger.
	 */
	AddonLogger getAddonLogger();
}
