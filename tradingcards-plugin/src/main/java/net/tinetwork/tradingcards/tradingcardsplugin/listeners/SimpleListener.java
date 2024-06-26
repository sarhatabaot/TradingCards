package net.tinetwork.tradingcards.tradingcardsplugin.listeners;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public abstract class SimpleListener implements Listener {
	protected final TradingCards plugin;
	private final Logger logger;

	protected SimpleListener(final @NotNull TradingCards plugin) {
		this.plugin = plugin;
		this.logger = plugin.getLogger();

		debug("registered.");
	}

	protected void debug(final String message) {
		plugin.debug(getClass(),message);
	}

	protected void info(final String message) {
		logger.info(() -> getClass().getSimpleName()+" "+message);
	}

	protected void warning(final String message){
		logger.warning(() -> getClass().getSimpleName()+" "+message);
	}


	protected void severe(final String message){
		logger.severe(() -> getClass().getSimpleName()+" "+message);
	}
}
