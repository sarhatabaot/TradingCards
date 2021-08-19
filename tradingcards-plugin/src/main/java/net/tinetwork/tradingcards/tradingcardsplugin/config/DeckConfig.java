package net.tinetwork.tradingcards.tradingcardsplugin.config;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.core.SimpleConfigFile;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class DeckConfig extends SimpleConfigFile {
	private static final String INVENTORY_PATH = "Decks.Inventories.";
	public DeckConfig(final TradingCards plugin) {
		super(plugin, "data/decks.yml");
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
	public List<String> getDeck(final UUID uuid, String deckNumber){
		if(containsDeck(uuid,deckNumber))
			return getAllDecks(uuid).getStringList(String.valueOf(deckNumber));
		return null;
	}

	public boolean containsDeck(final UUID uuid,String deckNumber) {
		if(containsPlayer(uuid))
			return getAllDecks(uuid).contains(String.valueOf(deckNumber));
		return false;
	}


	public boolean containsDeck(final UUID uuid,int deckNumber) {
		return containsDeck(uuid,String.valueOf(deckNumber));
	}

	public boolean containsCard(final UUID uuid,final String card, final String rarity){
		if(getAllDecks(uuid) == null || getAllDecks(uuid).getValues(false).isEmpty())
			return false;
		for(String deckNumber : getAllDecks(uuid).getValues(false).keySet()){
			for(String cardString: getDeck(uuid,deckNumber)) {
				String[] splitCardString = cardString.split(",");
				String rarityName = splitCardString[0];
				String cardName = splitCardString[1];
				String shiny = splitCardString[2];
				if(rarity.equalsIgnoreCase(rarityName) && card.equalsIgnoreCase(cardName) && shiny.equalsIgnoreCase("no"))
					return true;
			}
		}
		return false;
	}

	public boolean containsShinyCard(final UUID uuid,final String card, final String rarity) {
		if(getAllDecks(uuid) == null || getAllDecks(uuid).getValues(false).isEmpty())
			return false;
		for(String deckNumber : getAllDecks(uuid).getValues(false).keySet()){
			for(String cardString: getDeck(uuid,deckNumber)) {
				String[] splitCardString = cardString.split(",");
				String rarityName = splitCardString[0];
				String cardName = splitCardString[1];
				String shinyName = splitCardString[2];
				if(rarity.equalsIgnoreCase(rarityName) && card.equalsIgnoreCase(cardName) && shinyName.equalsIgnoreCase("yes"))
					return true;
			}
		}
		return false;
	}
}
