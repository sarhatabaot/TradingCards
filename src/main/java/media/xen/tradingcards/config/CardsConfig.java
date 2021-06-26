package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import media.xen.tradingcards.core.SimpleConfig;

import java.io.File;
import java.util.List;

public class CardsConfig{
	private List<SimpleCardsConfig> cardConfigs;
	public CardsConfig(final TradingCards plugin) {
		File cardsFolder = new File(plugin.getDataFolder().getPath()+"/cards");
		for(File file: cardsFolder.listFiles()) {
			if(file.getName().endsWith(".yml")) {
				cardConfigs.add(new SimpleCardsConfig(plugin, file.getName()));
				//load cards yml
			}
		}
	}

	public List<SimpleCardsConfig> getCardConfigs() {
		return cardConfigs;
	}
}
