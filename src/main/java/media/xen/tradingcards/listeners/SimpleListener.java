package media.xen.tradingcards.listeners;

import media.xen.tradingcards.TradingCards;
import org.bukkit.event.Listener;

import java.util.logging.Logger;

public abstract class SimpleListener implements Listener {
	protected final TradingCards plugin;
	private final Logger logger;

	public SimpleListener(final TradingCards plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();

		debug("registered.");
	}

	protected void debug(final String message) {
		plugin.debug(getClass().getSimpleName()+" "+message);
	}

	protected void info(final String message) {
		logger.info(getClass().getSimpleName()+" "+message);
	}

	protected void warning(final String message){
		logger.warning(getClass().getSimpleName()+" "+message);
	}


	protected void severe(final String message){
		logger.severe(getClass().getSimpleName()+" "+message);
	}
}
