package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DeckConfig extends SimpleConfig{
	private final String inventoryPath = "Decks.Inventories.";
	public DeckConfig(final TradingCards plugin) {
		super(plugin, "decks.yml");
	}

	public boolean containsPlayer(UUID uuid) {
		return getConfig().contains(inventoryPath+uuid.toString());
	}

	@Nullable
	public ConfigurationSection getInventory(UUID uuid) {
		if(containsPlayer(uuid))
			return getConfig().getConfigurationSection(inventoryPath+uuid.toString());
		return null;
	}
}
