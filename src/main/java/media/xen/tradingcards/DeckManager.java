package media.xen.tradingcards;


import de.tr7zw.nbtapi.NBTItem;
import media.xen.tradingcards.config.TradingCardsConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DeckManager {
	private static TradingCards plugin;

	public static void init(final TradingCards plugin) {
		DeckManager.plugin = plugin;
	}


	public static void openDeck(Player p, int deckNum) {
		String uuidString = p.getUniqueId().toString();
		plugin.debug("Deck UUID: " + uuidString);
		p.openInventory(generateDeckInventory(p, deckNum));
	}

	private static Inventory generateDeckInventory(final Player player, final int deckNum) {
		List<ItemStack> cards = addCards(player.getUniqueId(), deckNum);
		Inventory inv = Bukkit.createInventory(null, getDeckSize(), plugin.cMsg("&c" + player.getName() + "'s Deck #" + deckNum));
		for (ItemStack cardItem : cards) {
			inv.addItem(cardItem);
			plugin.debug("Item=" + cardItem.getType().toString() + ",amount=" + cardItem.getAmount() + ", added to inventory");
		}
		return inv;
	}


	private static List<ItemStack> addCards(final UUID uuid, final int deckNum) {
		List<String> contents = plugin.getDeckConfig().getConfig().getStringList("Decks.Inventories." + uuid.toString() + "." + deckNum);
		List<ItemStack> cards = new ArrayList<>();
		ItemStack card = null;
		boolean isNull = false;
		for (final String s : contents) {
			plugin.debug("Deck file content: " + s);

			String[] splitContents = s.split(",");
			final String rarity = splitContents[0];
			String cardName = splitContents[1];
			final int amount = Integer.parseInt(splitContents[2]);
			final String isShiny = splitContents[3];
			if (cardName == null) {
				cardName = "None";
			}
			if (rarity.isEmpty() || "BLANK".equalsIgnoreCase(rarity) || "None".equalsIgnoreCase(rarity))
				isNull = true;

			if (isShiny.equalsIgnoreCase("yes")) {
				card = CardManager.getCard(cardName, rarity, true);
				card.setAmount(amount);
			} else if (!isNull) {
				card = CardManager.getCard(cardName, rarity, amount);
			}

			if (isNull) {
				plugin.getLogger().warning("A null card has been found in a deck. It was truncated for safety.");
			} else {
				card.setAmount(amount);
				cards.add(card);
			}

			isNull = false;
		}
		return cards;
	}


	private static int getDeckSize() {
		if (plugin.getMainConfig().useLargeDecks)
			return 54;
		return 27;
	}

	@NotNull
	public static ItemStack createDeckItem(@NotNull final Player p, final int num) {
		ItemStack deck = TradingCardsConfig.getBlankDeck();
		ItemMeta deckMeta = deck.getItemMeta();
		deckMeta.setDisplayName(plugin.cMsg(plugin.getConfig().getString("General.Deck-Prefix") + p.getName() + "'s Deck #" + num));
		if (plugin.getConfig().getBoolean("General.Hide-Enchants", true)) {
			deckMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		deck.setItemMeta(deckMeta);
		deck.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
		return deck;
	}

	@NotNull
	public static ItemStack createDeck(@NotNull final Player player, final int num) {
		NBTItem nbtItem = new NBTItem(createDeckItem(player, num));
		nbtItem.setBoolean("isDeck", true);
		return nbtItem.getItem();
	}

	public static boolean isDeckMaterial(final Material material) {
		return material == Material.valueOf(plugin.getMainConfig().deckMaterial);
	}

	public static boolean isDeck(final ItemStack item) {
		return isDeckMaterial(item.getType()) && hasEnchantments(item) && new NBTItem(item).getBoolean("isDeck");
	}

	private static boolean hasEnchantments(final ItemStack item) {
		return item.containsEnchantment(Enchantment.DURABILITY) && item.getEnchantmentLevel(Enchantment.DURABILITY) == 10;
	}

	public static boolean hasDeck(@NotNull final Player p, final int num) {
		for (final ItemStack itemStack : p.getInventory()) {
			if (itemStack != null && isDeck(itemStack)) {
				String name = itemStack.getItemMeta().getDisplayName();
				String[] splitName = name.split("#");
				if (num == Integer.parseInt(splitName[1])) {
					return true;
				}
			}
		}

		return false;
	}
}
