package media.xen.tradingcards;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DeckManager {
	private static TradingCards plugin;


	public static void init(final TradingCards plugin){
		DeckManager.plugin = plugin;
	}


	public static void openDeck(Player p, int deckNum) {
		plugin.debug("Deck opened");
		String uuidString = p.getUniqueId().toString();
		plugin.debug("Deck UUID: " + uuidString);

		List<String> contents = plugin.getDeckConfig().getConfig().getStringList("Decks.Inventories." + uuidString + "." + deckNum);
		List<ItemStack> cards = new ArrayList<>();
		List<Integer> quantity = new ArrayList<>();
		ItemStack card = null;
		boolean isNull = false;

		for (final String s : contents) {
			plugin.debug("Deck file content: " + s);

			String[] splitContents = s.split(",");
			final String rarity = splitContents[0];
			final String cardName = splitContents[1];
			final String amount = splitContents[2];
			final String isShiny = splitContents[3];
			if (splitContents.length > 1) {
				if (splitContents[1] == null) {
					splitContents[1] = "None";
				}

				if (isShiny.equalsIgnoreCase("yes")) {
					if (!rarity.equalsIgnoreCase("BLANK") && !rarity.equalsIgnoreCase("None") && !rarity.isEmpty()) {
						card = CardManager.getCard(cardName,rarity,true);
						card.setAmount(Integer.parseInt(amount));
					} else {
						plugin.getLogger().warning("A null card has been found in a deck. It was truncated for safety.");
						isNull = true;
					}
				} else if (!rarity.equalsIgnoreCase("None") && !rarity.equalsIgnoreCase("BLANK") && !rarity.isEmpty()) {
					card = CardManager.getCard(cardName,rarity,Integer.parseInt(amount));
				} else {
					plugin.getLogger().warning("A null card has been found in a deck. It was truncated for safety.");
					isNull = true;
				}
			}

			if (!isNull) {
				cards.add(card);
			}

			if (splitContents.length > 1) {
				quantity.add(Integer.valueOf(splitContents[2]));
				plugin.debug("Put " + card + "," + splitContents[2] + " into respective lists.");
			} else {
				quantity.add(1);
			}

			isNull = false;
		}

		int invSlots = 27;
		if (plugin.getMainConfig().useLargeDecks) {
			invSlots = 54;
		}

		Inventory inv = Bukkit.createInventory(null, invSlots, plugin.cMsg("&c" + p.getName() + "'s Deck #" + deckNum));
		plugin.debug("Created inventory.");
		int iter = 0;

		for (Iterator<ItemStack> var12 = cards.iterator(); var12.hasNext(); ++iter) {
			ItemStack i = var12.next();
			plugin.debug("Item " + i.getType().toString() + " added to inventory!");
			i.setAmount(quantity.get(iter));
			inv.addItem(i);
		}

		p.openInventory(inv);
	}
}
