package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DeckConfig extends SimpleConfig{
	private static final String INVENTORY_PATH = "Decks.Inventories.";
	public DeckConfig(final TradingCards plugin) {
		super(plugin, "decks.yml");
	}

	public boolean containsPlayer(final UUID uuid) {
		return getConfig().contains(INVENTORY_PATH +uuid.toString());
	}

	@Nullable
	public ConfigurationSection getAllDecks(final UUID uuid) {
		if(containsPlayer(uuid))
			return getConfig().getConfigurationSection(INVENTORY_PATH +uuid.toString());
		return null;
	}

	@Nullable
	public ConfigurationSection getDeck(final UUID uuid, int deckNumber){
		if(containsDeck(uuid,deckNumber))
			return getAllDecks(uuid).getConfigurationSection(String.valueOf(deckNumber));
		return null;
	}

	public boolean containsDeck(final UUID uuid,int deckNumber) {
		if(containsPlayer(uuid))
			return getAllDecks(uuid).contains(String.valueOf(deckNumber));
		return false;
	}
}
