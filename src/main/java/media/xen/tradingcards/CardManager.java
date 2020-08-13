package media.xen.tradingcards;


import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class CardManager {
	private final TradingCards plugin;
	private Map<String,ItemStack> cards;

	public CardManager(final TradingCards plugin) {
		this.plugin = plugin;
	}

	private void initialize(){

	}


	public ItemStack getCard(final String cardName){
		return cards.get(cardName.toLowerCase());
	}

	public static class CardBuilder {

	}
}
