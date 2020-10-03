package media.xen.tradingcards.db;

import media.xen.tradingcards.TradingCards;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DbUtil {
	private static TradingCards plugin;

	public static void init(final TradingCards plugin){
		DbUtil.plugin = plugin;
	}

	public static void convertToDb() {
		ConfigurationSection cards = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards");
		Set<String> cardKeys = cards.getKeys(false);
		Iterator<String> var3 = cardKeys.iterator();

		String series;
		String type;
		while (var3.hasNext()) {
			String key = var3.next();
			ConfigurationSection cardsWithKey = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + key);
			Set<String> keyKeys = cardsWithKey.getKeys(false);

			for (final String key2 : keyKeys) {
				String cost = "None";
				series = plugin.getCardsConfig().getConfig().getString("Cards." + key + "." + key2 + ".Series");
				String about = plugin.getCardsConfig().getConfig().getString("Cards." + key + "." + key2 + ".About", "None");
				type = plugin.getCardsConfig().getConfig().getString("Cards." + key + "." + key2 + ".Type");
				String info = plugin.getCardsConfig().getConfig().getString("Cards." + key + "." + key2 + ".Info");
				if (plugin.getCardsConfig().getConfig().contains("Cards." + key + "." + key2 + ".Buy-Price")) {
					cost = String.valueOf(plugin.getCardsConfig().getConfig().getDouble("Cards." + key + "." + key2 + ".Buy-Price"));
				}

				if (!plugin.exists("SELECT * FROM cards WHERE rarity = '" + key + "' AND name = '" + key2 + "' AND about = '" + about + "' AND series = '" + series + "' AND type = '" + type + "' AND info = '" + info + "' AND price = '" + cost + "'")) {
					if (plugin.getDatabase("trading_cards").executeStatement("INSERT INTO cards (rarity, name, about, series, type, info, price) VALUES ('" + key + "', '" + key2 + "', '" + about + "', '" + series + "', '" + type + "', '" + info + "', '" + cost + "')")) {
						plugin.debug(key + ", " + key2 + " - Added to SQLite!");
					} else {
						plugin.debug(key + ", " + key2 + " - Unable to be added!");
					}
				}
			}
		}

		ConfigurationSection decks =plugin.getDeckConfig().getConfig().getConfigurationSection("Decks.Inventories");
		Set<String> deckKeys = decks.getKeys(false);
		int deckNum = 0;
		Iterator<String> var18 = deckKeys.iterator();
		String s;

		while (var18.hasNext()) {
			String key = var18.next();
			plugin.debug("Deck key is: " + key);

			ConfigurationSection deckList = plugin.getDeckConfig().getConfig().getConfigurationSection("Decks.Inventories." + key);
			if (deckList != null) {

				for (final String value : deckList.getKeys(false)) {
					s = value;
					deckNum += Integer.parseInt(s);

					plugin.debug("Deck running total: " + deckNum);
				}
			}

			if (deckNum == 0) {
				plugin.debug("No deck?!");
			} else {
				plugin.debug("Decks:" + deckNum);
				label127:
				for (int i = 0; i < deckNum; ++i) {
					List<String> contents = plugin.getDeckConfig().getConfig().getStringList("Decks.Inventories." + key + "." + deckNum);
					Iterator var24 = contents.iterator();

					while (true) {
						while (true) {
							String[] splitContents;
							do {
								if (!var24.hasNext()) {
									continue label127;
								}

								s = (String) var24.next();
								plugin.debug("Deck content: " + s);
								splitContents = s.split(",");
							} while (splitContents.length <= 1);

							if (splitContents[1] == null) {
								splitContents[1] = "None";
							}

							Integer cardID = (Integer) plugin.getDatabase("trading_cards").queryValue("SELECT id FROM cards WHERE name = '" + splitContents[1] + "' AND rarity = '" + splitContents[0] + "'", "ID");
							if (splitContents[3].equalsIgnoreCase("yes")) {
								if (!splitContents[0].equalsIgnoreCase("BLANK") && !splitContents[1].equalsIgnoreCase("None") && splitContents[1] != null && !splitContents[1].isEmpty() && plugin.getDatabase("trading_cards").queryValue("SELECT * FROM decks WHERE uuid = '" + key + "' AND deckID = '" + deckNum + "' AND card = '" + cardID + "' AND isShiny = 1", "ID") == null && !plugin.getDatabase("trading_cards").executeStatement("INSERT INTO decks (uuid, deckID, card, isShiny, count) VALUES ('" + key + "', '" + deckNum + "', '" + cardID + "', 1, " + Integer.valueOf(splitContents[2]) + ")") && plugin.getConfig().getBoolean("General.Debug-Mode")) {
									System.out.println("[Cards] Error adding shiny card to deck SQLite, check stack!");
								}
							} else if (!splitContents[1].equalsIgnoreCase("None") && !splitContents[0].equalsIgnoreCase("BLANK") && splitContents[1] != null && !splitContents[1].isEmpty()) {
								if (plugin.getDatabase("trading_cards").queryValue("SELECT * FROM decks WHERE uuid = '" + key + "' AND deckID = '" + deckNum + "' AND card = '" + cardID + "' AND isShiny = 0", "ID") == null && !plugin.getDatabase("trading_cards").executeStatement("INSERT INTO decks (uuid, deckID, card, isShiny, count) VALUES ('" + key + "', '" + deckNum + "', '" + cardID + "', 0, " + Integer.valueOf(splitContents[2]) + ")") && plugin.getConfig().getBoolean("General.Debug-Mode")) {
									System.out.println("[Cards] Error adding card to deck SQLite, check stack!");
								}
							} else {
								System.out.println("[Cards] Warning! A null card has been found in a deck. It was truncated for safety.");
							}
						}
					}
				}
			}
		}

	}
}
