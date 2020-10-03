package media.xen.tradingcards.addons;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author sarhatabaot
 */
public interface TradingCardsAddon {
	/**
	 * Return an instance of your plugin.
	 * @return an instance of your plugin.
	 */
	JavaPlugin getJavaPlugin();

	/**
	 *
	 * @return the addon logger.
	 */
	AddonLogger getAddonLogger();
}
