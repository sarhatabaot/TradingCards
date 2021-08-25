package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CardsConfig{
	private final List<SimpleCardsConfig> cardConfigs;

	public CardsConfig(final TradingCards plugin) {
		this.cardConfigs = new ArrayList<>();
		createCardsFolder(plugin);
		createDefaultCardConfig(plugin);
		File cardsFolder = new File(plugin.getDataFolder().getPath()+File.separator+"cards");

		for(File file: cardsFolder.listFiles()) {
			plugin.debug(file.getName());
			if(file.getName().endsWith(".yml")) {
				plugin.debug("Added: "+file.getName());
				try {
					cardConfigs.add(new SimpleCardsConfig(plugin, file.getName()));
				}catch (ConfigurateException e) {
					plugin.getLogger().severe(e.getMessage());
				}
			}
		}
	}
	private static void createCardsFolder(final TradingCards plugin) {
		final File cardsFolder = new File(plugin.getDataFolder()+File.separator+"cards");
		if(!cardsFolder.exists())
			cardsFolder.mkdir();
	}

	private static void createDefaultCardConfig(final TradingCards plugin) {
		try {
			new SimpleCardsConfig(plugin, "cards.yml").saveDefaultConfig();
		} catch (ConfigurateException e) {
			plugin.getLogger().severe(e.getMessage());
		}
	}
	public List<SimpleCardsConfig> getCardConfigs() {
		return cardConfigs;
	}
}
