package net.tinetwork.tradingcards.api.addons;

import org.bukkit.event.Listener;

public class AddonListener implements Listener {
	protected final TradingCardsAddon addon;

	public AddonListener(final TradingCardsAddon tradingCardsAddon) {
		this.addon = tradingCardsAddon;
	}

}
