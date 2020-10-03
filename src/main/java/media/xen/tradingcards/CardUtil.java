package media.xen.tradingcards;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public class CardUtil {
	private static TradingCards plugin;

	public static void init(final TradingCards plugin) {
		CardUtil.plugin = plugin;
	}

	public String upgradeRarity(String packName, String rarity) {
		plugin.debug("Starting booster pack upgrade check - Current rarity is " + rarity + "!");
		ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("Rarities");
		Set<String> rarityKeys = rarities.getKeys(false);
		Map<Integer, String> rarityMap = new HashMap<>();
		int i = 0;
		int curRarity = 0;
		for (String key : rarityKeys) {
			rarityMap.put(i, key);
			if (key.equalsIgnoreCase(rarity)) curRarity = i;
			plugin.debug("Rarity " + i + " is " + key);
			i++;
		}
		int chance = plugin.getConfig().getInt("BoosterPacks." + packName + ".UpgradeChance", 0);
		if (chance <= 0) {
			plugin.debug("Pack has upgrade chance set to 0! Exiting..");
			return rarityMap.get(curRarity);
		}
		int random = plugin.r.nextInt(100000) + 1;
		if (random <= chance) {
			if (curRarity < i) curRarity++;
			plugin.debug("Card upgraded! new rarity is " + rarityMap.get(curRarity) + "!");
			return rarityMap.get(curRarity);
		}
		plugin.debug("Card not upgraded! Rarity remains at " + rarityMap.get(curRarity) + "!");
		return rarityMap.get(curRarity);
	}


	/**
	 * Drops an item at the player's location.
	 *
	 * @param player Player
	 * @param item   Item
	 */
	public static void dropItem(final Player player, final ItemStack item) {
		if (player.getInventory().firstEmpty() != -1) {
			player.getInventory().addItem(item);
		} else {
			World curWorld4 = player.getWorld();
			if (player.getGameMode() == GameMode.SURVIVAL) {
				curWorld4.dropItem(player.getLocation(), item);
			}
		}
	}

	@NotNull
	public static ItemStack generateCard(String cardName, String rarityName, boolean forcedShiny) {
		if (rarityName.equals("None")) {
			return null;
		}
		//plugin.reloadAllConfig();
		plugin.debug("generateCard.cardSection: " + plugin.getCardsConfig().getConfig().contains("Cards." + rarityName));
		plugin.debug("generateCard.rarity: " + rarityName);

		CardManager.CardBuilder builder = new CardManager.CardBuilder(cardName);
		boolean hasShinyVersion = plugin.getCardsConfig().getConfig().getBoolean("Cards." + rarityName + "." + cardName + ".Has-Shiny-Version");
		boolean isShiny = false;
		if (hasShinyVersion) {
			int shinyRandom = plugin.r.nextInt(100) + 1;
			if (shinyRandom <= plugin.getConfig().getInt("Chances.Shiny-Version-Chance")) {
				isShiny = true;
			}
		}

		if (forcedShiny) {
			isShiny = true;
		}
		String rarityColour = plugin.getConfig().getString("Rarities." + rarityName + ".Colour");
		String prefix = plugin.getConfig().getString("General.Card-Prefix");
		String series = plugin.getCardsConfig().getConfig().getString("Cards." + rarityName + "." + cardName + ".Series");
		String seriesColour = plugin.getConfig().getString("Colours.Series");
		String seriesDisplay = plugin.getConfig().getString("DisplayNames.Cards.Series", "Series");
		String about = plugin.getCardsConfig().getConfig().getString("Cards." + rarityName + "." + cardName + ".About", "None");
		String aboutColour = plugin.getConfig().getString("Colours.About");
		String aboutDisplay = plugin.getConfig().getString("DisplayNames.Cards.About", "About");
		String type = plugin.getCardsConfig().getConfig().getString("Cards." + rarityName + "." + cardName + ".Type");
		String typeColour = plugin.getConfig().getString("Colours.Type");
		String typeDisplay = plugin.getConfig().getString("DisplayNames.Cards.Type", "Type");
		String info = plugin.getCardsConfig().getConfig().getString("Cards." + rarityName + "." + cardName + ".Info");
		String infoColour = plugin.getConfig().getString("Colours.Info");
		String infoDisplay = plugin.getConfig().getString("DisplayNames.Cards.Info", "Info");
		String shinyPrefix = plugin.getConfig().getString("General.Shiny-Name");
		String cost;
		if (plugin.getCardsConfig().getConfig().contains("Cards." + rarityName + "." + cardName + ".Buy-Price")) {
			cost = String.valueOf(plugin.getCardsConfig().getConfig().getDouble("Cards." + rarityName + "." + cardName + ".Buy-Price"));
		} else {
			cost = "None";
		}

		boolean isPlayerCard = false;
		if (plugin.isPlayerCard(cardName)) {
			isPlayerCard = true;
		}

		return builder.isShiny(isShiny)
				.rarityColour(rarityColour)
				.prefix(prefix)
				.series(series, seriesColour, seriesDisplay)
				.about(about, aboutColour, aboutDisplay)
				.type(type, typeColour, typeDisplay)
				.info(info, infoColour, infoDisplay)
				.shinyPrefix(shinyPrefix)
				.isPlayerCard(isPlayerCard)
				.cost(cost)
				.rarity(rarityName).build();
	}

	/**
	 * Generates a random card.
	 *
	 * @param rarityName
	 * @param forcedShiny
	 * @return
	 * @deprecated Should not actually be used. Use {@link CardUtil#getRandomCard(String, boolean)}
	 */
	public static ItemStack generateRandomCard(String rarityName, boolean forcedShiny) {
		ConfigurationSection cardSection = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + rarityName);
		plugin.debug("generateCard.cardSection: " + plugin.getCardsConfig().getConfig().contains("Cards." + rarityName));
		plugin.debug("generateCard.rarity: " + rarityName);

		Set<String> cards = cardSection.getKeys(false);
		List<String> cardNames = new ArrayList<>(cards);
		int cIndex = plugin.r.nextInt(cardNames.size());
		String cardName = cardNames.get(cIndex);
		return generateCard(cardName, rarityName, forcedShiny);
	}

	@NotNull
	public static ItemStack getRandomCard(final String rarityName, final boolean forcedShiny) {
		ConfigurationSection cardSection = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + rarityName);
		plugin.debug("generateCard.cardSection: " + plugin.getCardsConfig().getConfig().contains("Cards." + rarityName));
		plugin.debug("generateCard.rarity: " + rarityName);

		Set<String> cards = cardSection.getKeys(false);
		List<String> cardNames = new ArrayList<>(cards);
		int cIndex = plugin.r.nextInt(cardNames.size());
		String cardName = cardNames.get(cIndex);
		return CardManager.getCard(cardName, rarityName);
	}

	public static String getCardName(String rarity, String display) {
		boolean hasPrefix = false;
		String prefix = "";
		if (plugin.getConfig().contains("General.Card-Prefix") && !plugin.getConfig().getString("General.Card-Prefix").equals("")) {
			hasPrefix = true;
			prefix = ChatColor.stripColor(plugin.getConfig().getString("General.Card-Prefix"));
		}
		String shinyPrefix = plugin.getConfig().getString("General.Shiny-Name");
		String cleaned = ChatColor.stripColor(display);
		if (hasPrefix) cleaned = cleaned.replaceAll(prefix, "");
		cleaned = cleaned.replaceAll(shinyPrefix + " ", "");
		String[] cleanedArray = cleaned.split(" ");
		ConfigurationSection cs = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + rarity);
		Set<String> keys = cs.getKeys(false);
		for (String s : keys) {
			plugin.debug("getCardName s: " + s);
			plugin.debug("getCardName display: " + display);
			if (cleanedArray.length > 1) {
				plugin.debug("cleanedArray > 1");
				if ((cleanedArray[0] + "_" + cleanedArray[1]).matches(s)) return s;
				if ((cleanedArray[0] + " " + cleanedArray[1]).matches(s)) return s;
				if (cleanedArray.length > 2 && (cleanedArray[1] + "_" + cleanedArray[2]).matches(s)) return s;
				if (cleanedArray.length > 2 && (cleanedArray[1] + " " + cleanedArray[2]).matches(s)) return s;
				if (cleanedArray.length > 3 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3]).matches(s))
					return s;
				if (cleanedArray.length > 3 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3]).matches(s))
					return s;
				if (cleanedArray.length > 4 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3] + "_" + cleanedArray[4]).matches(s))
					return s;
				if (cleanedArray.length > 4 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3] + " " + cleanedArray[4]).matches(s))
					return s;
				if (cleanedArray.length > 5 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3] + "_" + cleanedArray[4] + "_" + cleanedArray[5]).matches(s))
					return s;
				if (cleanedArray.length > 5 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3] + " " + cleanedArray[4] + " " + cleanedArray[5]).matches(s))
					return s;
				if (cleanedArray.length > 6 && (cleanedArray[1] + "_" + cleanedArray[2] + "_" + cleanedArray[3] + "_" + cleanedArray[4] + "_" + cleanedArray[5] + "_" + cleanedArray[6]).matches(s))
					return s;
				if (cleanedArray.length > 6 && (cleanedArray[1] + " " + cleanedArray[2] + " " + cleanedArray[3] + " " + cleanedArray[4] + " " + cleanedArray[5] + " " + cleanedArray[6]).matches(s))
					return s;
				if (cleanedArray.length == 2 && cleanedArray[1].matches(s)) return s;
			}
		}
		return "None";
	}

	private static String getMobType(EntityType e, int shouldItDrop, boolean alwaysDrop) {
		if (plugin.isMobHostile(e)) {
			if (!alwaysDrop) {
				if (shouldItDrop > plugin.getConfig().getInt("Chances.Hostile-Chance")) {
					return "None";
				}
			}
			return "Hostile";
		}
		if (plugin.isMobNeutral(e)) {
			if (!alwaysDrop) {
				if (shouldItDrop > plugin.getConfig().getInt("Chances.Neutral-Chance")) {
					return "None";
				}

			}
			return "Neutral";
		}
		if (plugin.isMobPassive(e)) {
			if (!alwaysDrop) {
				if (shouldItDrop > plugin.getConfig().getInt("Chances.Passive-Chance")) {
					return "None";
				}

			}
			return "Passive";
		}

		if (!plugin.isMobBoss(e)) {
			return "None";
		}

		if (!alwaysDrop) {
			if (shouldItDrop > plugin.getConfig().getInt("Chances.Boss-Chance")) {
				return "None";
			}

			//TODO: Implement this
			if (plugin.getConfig().getBoolean("Chances.Boss-Drop")) {
				int var16 = plugin.getConfig().getInt("Chances.Boss-Drop-Rarity");
			}

		}
		return "Boss";

	}

	public static String calculateRarity(EntityType e, boolean alwaysDrop) {
		int shouldItDrop = plugin.r.nextInt(100) + 1;
		String type = getMobType(e, shouldItDrop, alwaysDrop);
		plugin.debug("shouldItDrop Num: " + shouldItDrop);

		ConfigurationSection rarities = plugin.getConfig().getConfigurationSection("Rarities");
		Set<String> rarityKeys = rarities.getKeys(false);
		Map<String, Integer> rarityChances = new HashMap<>();
		Map<Integer, String> rarityIndexes = new HashMap<>();
		int i = 0;
		int mini = 0;
		int random = plugin.r.nextInt(100000) + 1;
		plugin.debug("Random Card Num: " + random);
		plugin.debug("Type: " + type);

		String key;
		int chance;
		for (Iterator<String> var13 = rarityKeys.iterator(); var13.hasNext(); rarityChances.put(key, chance)) {
			key = var13.next();
			rarityIndexes.put(i, key);
			++i;

			plugin.debug(i + ", " + key);


			if (plugin.getConfig().contains("Chances." + key + "." + StringUtils.capitalize(e.getKey().getKey())) && mini == 0) {
				plugin.debug("Mini: " + i);
				mini = i;
			}

			chance = plugin.getConfig().getInt("Chances." + key + "." + type, -1);
			plugin.debug("Keys: " + key + ", " + chance + ", i=" + i);
		}

		if (mini != 0) {
			plugin.debug("Mini: " + mini);
			plugin.debug("i: " + i);

			while (i >= mini) {
				--i;
				plugin.debug("i: " + i);


				chance = plugin.getConfig().getInt("Chances." + rarityIndexes.get(i) + "." + StringUtils.capitalize(e.getKey().getKey()), -1);
				plugin.debug("Chance: " + chance);
				plugin.debug("Rarity: " + rarityIndexes.get(i));

				if (chance > 0 && random <= chance) {
					return rarityIndexes.get(i);
				}
			}
		} else {
			while (i > 0) {
				--i;
				plugin.debug("Iteration " + i + " in HashMap is: " + rarityIndexes.get(i) + ", " + plugin.getConfig().getString("Rarities." + rarityIndexes.get(i) + ".Name"));

				chance = plugin.getConfig().getInt("Chances." + rarityIndexes.get(i) + "." + type, -1);
				plugin.debug(plugin.getConfig().getString("Rarities." + rarityIndexes.get(i) + ".Name") + "'s chance of dropping: " + chance + " out of 100,000");

				if (chance > 0 && random <= chance) {
					plugin.debug("Giving a " + plugin.getConfig().getString("Rarities." + rarityIndexes.get(i) + ".Name") + " card.");
					return rarityIndexes.get(i);
				}
			}
		}

		return "None";
	}
}
