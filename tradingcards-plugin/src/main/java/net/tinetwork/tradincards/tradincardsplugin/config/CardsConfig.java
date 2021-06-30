package net.tinetwork.tradincards.tradincardsplugin.config;

import net.tinetwork.tradincards.tradincardsplugin.TradingCards;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardsConfig{
	private final List<SimpleCardsConfig> cardConfigs;
	private final TradingCards plugin;

	public CardsConfig(final TradingCards plugin) {
		this.plugin = plugin;
		this.cardConfigs = new ArrayList<>();
		createCardsFolder(plugin);
		createDefaultCardConfig(plugin);
		File cardsFolder = new File(plugin.getDataFolder().getPath()+File.separator+"cards");

		for(File file: cardsFolder.listFiles()) {
			plugin.debug(file.getName());
			if(file.getName().endsWith(".yml")) {
				plugin.debug("Added: "+file.getName());
				cardConfigs.add(new SimpleCardsConfig(plugin, file.getName()));
			}
		}
	}
	private static void createCardsFolder(final TradingCards plugin) {
		final File cardsFolder = new File(plugin.getDataFolder()+File.separator+"cards");
		if(!cardsFolder.exists())
			cardsFolder.mkdir();
	}

	private static void createDefaultCardConfig(final TradingCards plugin) {
		new SimpleCardsConfig(plugin, "cards/cards.yml").saveDefaultConfig();
	}
	public List<SimpleCardsConfig> getCardConfigs() {
		return cardConfigs;
	}
}
