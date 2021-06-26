package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardsConfig{
	private final List<SimpleCardsConfig> cardConfigs;
	private TradingCards plugin;
	public CardsConfig(final TradingCards plugin) {
		this.plugin = plugin;
		this.cardConfigs = new ArrayList<>();
		File cardsFolder = new File(plugin.getDataFolder().getPath()+"/cards");

		for(File file: cardsFolder.listFiles()) {
			plugin.debug(file.getName());
			if(file.getName().endsWith(".yml")) {
				plugin.debug("Added: "+file.getName());
				cardConfigs.add(new SimpleCardsConfig(plugin, file.getName()));
				//load cards yml
			}
		}
	}

	public List<SimpleCardsConfig> getCardConfigs() {
		return cardConfigs;
	}
}
