package media.xen.tradingcards.addons;

import media.xen.tradingcards.TradingCards;

public abstract class AddonLogger {
	private final String addonName;
	private final TradingCards tradingCards;

	public AddonLogger(final String addonName, final TradingCards tradingCards) {
		this.addonName = addonName;
		this.tradingCards = tradingCards;
	}

	public void info(String message) {
		tradingCards.getLogger().info(addonName+" "+message);
	}

	public void debug(String message){
		tradingCards.debug(addonName +" "+message);
	}
}
