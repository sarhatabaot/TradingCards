package media.xen.tradingcards.config;

import media.xen.tradingcards.TradingCards;
import media.xen.tradingcards.core.SimpleConfig;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CardsConfig extends SimpleConfig {
	public CardsConfig(final TradingCards plugin) {
		super(plugin, "cards.yml");
	}

	private int getDeckAmount(ConfigurationSection deckList) {
		int deckNumber = 0;
		for (String s : deckList.getKeys(false)) {
			deckNumber += Integer.parseInt(s);
			plugin.debug("Deck running total: " + deckNumber);
		}
		return deckNumber;
	}

	public boolean deleteRarity(final Player player, final String rarity) {
		if (plugin.isRarityAndFormat(rarity).equalsIgnoreCase("None"))
			return false;


		ConfigurationSection cards = plugin.getConfig().getConfigurationSection("Cards." + plugin.isRarityAndFormat(rarity));
		Set<String> cardKeys = cards.getKeys(false);
		int numCardsCounter = 0;

		for (final String key : cardKeys) {
			plugin.debug("deleteRarity iteration: " + numCardsCounter);

			if (plugin.hasShiny(player, key, rarity) && !plugin.hasCard(player, key, rarity)) {
				plugin.debug("Deleted: Cards." + key + ".key2");

				plugin.deleteCard(player, key, rarity);
				++numCardsCounter;
			}

			if (plugin.hasCard(player, key, rarity)) {
				plugin.debug("Deleted: Cards." + key + ".key2");

				plugin.deleteCard(player, key, rarity);
				++numCardsCounter;
			}
		}
		return true;


	}

	public boolean deleteCard(Player p, String card, String rarity) {
		if (!plugin.hasCard(p, card, rarity)) {
			plugin.debug("Player=" + p.getName() + ",doesn't have card=" + card + ",rarity=" + rarity);
			return false;
		}

		String uuidString = p.getUniqueId().toString();

		ConfigurationSection deckList = plugin.getDeckConfig().getConfig().getConfigurationSection("Decks.Inventories." + uuidString);
		if (deckList == null) {
			plugin.debug("this deck doesn't exist or there was a problem accessing the file.");
			return false;
		}

		int deckAmount = getDeckAmount(deckList);
		if (deckAmount == 0) {
			plugin.debug("deckNumber=0");
			return false;
		}

		plugin.debug("Decks:" + deckAmount);
		for (int i = 0; i < deckAmount; i++) {
			if (plugin.getDeckConfig().getConfig().contains("Decks.Inventories." + uuidString + "." + (i + 1))) {
				List<String> contents = plugin.getDeckConfig().getConfig().getStringList("Decks.Inventories." + uuidString + "." + (i + 1));
				List<String> contentsNew = new ArrayList<>();
				for (String s2 : contents) {
					String[] splitContents = s2.split(",");
					if (getConfig().getBoolean("General.Eat-Shiny-Cards") && splitContents[3].equalsIgnoreCase("yes")) {
						plugin.debug("Eat-Shiny-Cards is true and card is shiny!");
						if (splitContents[0].equalsIgnoreCase(rarity)) {
							if (splitContents[1].equalsIgnoreCase(card)) {
								if (Integer.parseInt(splitContents[2]) <= 1) continue;
								int number = Integer.parseInt(splitContents[2]);
								splitContents[2] = String.valueOf(number - 1);
								StringBuilder strBuilder = new StringBuilder();
								for (final String splitContent : splitContents) {
									strBuilder.append(splitContent);
									strBuilder.append(",");
								}
								String newString = strBuilder.substring(0, strBuilder.length() - 1);
								contentsNew.add(newString);
								continue;
							}
							contentsNew.add(s2);
							continue;
						}
						contentsNew.add(s2);
						continue;
					}
					if (getConfig().getBoolean("General.Eat-Shiny-Cards") && splitContents[3].equalsIgnoreCase("no")) {
						plugin.debug("Eat-Shiny-Cards is true and card is not shiny!");
						if (splitContents[0].equalsIgnoreCase(rarity)) {
							if (splitContents[1].equalsIgnoreCase(card)) {
								if (Integer.parseInt(splitContents[2]) <= 1) continue;
								int number = Integer.parseInt(splitContents[2]);
								splitContents[2] = String.valueOf(number - 1);
								StringBuilder strBuilder = new StringBuilder();
								for (final String splitContent : splitContents) {
									strBuilder.append(splitContent);
									strBuilder.append(",");
								}
								String newString = strBuilder.substring(0, strBuilder.length() - 1);
								contentsNew.add(newString);
								continue;
							}
							contentsNew.add(s2);
							continue;
						}
						contentsNew.add(s2);
						continue;
					}
					if (!getConfig().getBoolean("General.Eat-Shiny-Cards") && splitContents[3].equalsIgnoreCase("yes")) {
						plugin.debug("Eat-Shiny-Cards is false and card is shiny!");
						if (!splitContents[0].equalsIgnoreCase(rarity)) continue;
						plugin.debug("Adding card..");
						contentsNew.add(s2);
						continue;
					}
					if (getConfig().getBoolean("General.Eat-Shiny-Cards") || !splitContents[3].equalsIgnoreCase("no"))
						continue;
					plugin.debug("Eat-Shiny-Cards is false and card is not shiny!");
					if (splitContents[0].equalsIgnoreCase(rarity)) {
						if (splitContents[1].equalsIgnoreCase(card)) {
							if (Integer.parseInt(splitContents[2]) <= 1) continue;
							int number = Integer.parseInt(splitContents[2]);
							splitContents[2] = String.valueOf(number - 1);
							StringBuilder strBuilder = new StringBuilder();
							for (final String splitContent : splitContents) {
								strBuilder.append(splitContent);
								strBuilder.append(",");
							}
							String newString = strBuilder.substring(0, strBuilder.length() - 1);
							contentsNew.add(newString);
							continue;
						}
						contentsNew.add(s2);
						continue;
					}
					contentsNew.add(s2);
				}
				plugin.getDeckConfig().getConfig().set("Decks.Inventories." + uuidString + "." + (i + 1), contentsNew);
				plugin.getDeckConfig().saveConfig();
				plugin.reloadAllConfig();
				contentsNew.clear();
			}
		}


		return true;
	}
}
