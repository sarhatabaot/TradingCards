package media.xen.tradingcards.api.addons;

import org.bukkit.event.Listener;

public class AddonListener implements Listener {
	protected TradingCardsAddon tradingCardsAddon;

	public AddonListener(final TradingCardsAddon tradingCardsAddon) {
		this.tradingCardsAddon = tradingCardsAddon;
	}

}
