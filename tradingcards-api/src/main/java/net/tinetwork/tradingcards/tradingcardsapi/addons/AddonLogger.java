package net.tinetwork.tradingcards.tradingcardsapi.addons;

import net.tinetwork.tradingcards.tradingcardsapi.TradingCardsPlugin;

public class AddonLogger {
	private final String addonName;
	private final TradingCardsPlugin tradingCards;

	public AddonLogger(final String addonName, final TradingCardsPlugin tradingCards) {
		this.addonName = addonName;
		this.tradingCards = tradingCards;
	}

	public void severe(String message){
		tradingCards.getLogger().severe(addonName+" "+message);
	}

	public void warning(String message){
		tradingCards.getLogger().warning(addonName+" "+message);
	}

	public void info(String message) {
		tradingCards.getLogger().info(addonName+" "+message);
	}

	public void debug(String message){
		tradingCards.debug(addonName +" "+message);
	}
}
