package net.tinetwork.tradingcards.api.addons;

import org.bukkit.event.Listener;

public class AddonListener implements Listener {
	protected TradingCardsAddon addon;

	public AddonListener(final TradingCardsAddon tradingCardsAddon) {
		this.addon = tradingCardsAddon;
	}

}
