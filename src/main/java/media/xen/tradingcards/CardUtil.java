package media.xen.tradingcards;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sarhatabaot
 */
public class CardUtil {
	private static TradingCards plugin;

	public CardUtil(final TradingCards plugin) {
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

	public static String calculateRarity(){
		return null;
	}

	public ItemStack generateCard(String rare, boolean forcedShiny) {
		if (rare.equals("None")) {
			return null;
		}

		plugin.debug("generateCard.rare: " + rare);

		ItemStack card = plugin.getBlankCard(1);
		plugin.reloadAllConfig();
		ConfigurationSection cardSection = plugin.getCardsConfig().getConfig().getConfigurationSection("Cards." + rare);
		plugin.debug("generateCard.cardSection: " + plugin.getCardsConfig().getConfig().contains("Cards." + rare));
		plugin.debug("generateCard.rarity: " + rare);

		Set<String> cards = cardSection.getKeys(false);
		List<String> cardNames = new ArrayList<>(cards);
		int cIndex = plugin.r.nextInt(cardNames.size());
		String cardName = cardNames.get(cIndex);
		boolean hasShinyVersion = plugin.getCardsConfig().getConfig().getBoolean("Cards." + rare + "." + cardName + ".Has-Shiny-Version");
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

		String rarityColour = plugin.getConfig().getString("Rarities." + rare + ".Colour");
		String prefix = plugin.getConfig().getString("General.Card-Prefix");
		String series = plugin.getCardsConfig().getConfig().getString("Cards." + rare + "." + cardName + ".Series");
		String seriesColour = plugin.getConfig().getString("Colours.Series");
		String seriesDisplay = plugin.getConfig().getString("DisplayNames.Cards.Series", "Series");
		String about = plugin.getCardsConfig().getConfig().getString("Cards." + rare + "." + cardName + ".About", "None");
		String aboutColour = plugin.getConfig().getString("Colours.About");
		String aboutDisplay = plugin.getConfig().getString("DisplayNames.Cards.About", "About");
		String type = plugin.getCardsConfig().getConfig().getString("Cards." + rare + "." + cardName + ".Type");
		String typeColour = plugin.getConfig().getString("Colours.Type");
		String typeDisplay = plugin.getConfig().getString("DisplayNames.Cards.Type", "Type");
		String info = plugin.getCardsConfig().getConfig().getString("Cards." + rare + "." + cardName + ".Info");
		String infoColour = plugin.getConfig().getString("Colours.Info");
		String infoDisplay = plugin.getConfig().getString("DisplayNames.Cards.Info", "Info");
		String shinyPrefix = plugin.getConfig().getString("General.Shiny-Name");
		String cost;
		if (plugin.getCardsConfig().getConfig().contains("Cards." + rare + "." + cardName + ".Buy-Price")) {
			cost = String.valueOf(plugin.getCardsConfig().getConfig().getDouble("Cards." + rare + "." + cardName + ".Buy-Price"));
		} else {
			cost = "None";
		}

		ItemMeta cmeta = card.getItemMeta();
		boolean isPlayerCard = false;
		if (plugin.isPlayerCard(cardName)) {
			isPlayerCard = true;
		}

		if (isShiny) {
			if (!isPlayerCard) {
				cmeta.setDisplayName(plugin.cMsg(plugin.getConfig().getString("DisplayNames.Cards.ShinyTitle").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("%SHINYPREFIX%", shinyPrefix).replaceAll("_", " ")));
			} else {
				cmeta.setDisplayName(plugin.cMsg(plugin.getConfig().getString("DisplayNames.Cards.ShinyTitle").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("%SHINYPREFIX%", shinyPrefix)));
			}
		} else if (!isPlayerCard) {
			cmeta.setDisplayName(plugin.cMsg(plugin.getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost).replaceAll("_", " ")));
		} else {
			cmeta.setDisplayName(plugin.cMsg(plugin.getConfig().getString("DisplayNames.Cards.Title").replaceAll("%PREFIX%", prefix).replaceAll("%COLOUR%", rarityColour).replaceAll("%NAME%", cardName).replaceAll("%COST%", cost)));
		}

		List<String> lore = new ArrayList<>();
		lore.add(plugin.cMsg(typeColour + typeDisplay + ": &f" + type));
		if (!info.equals("None") && !info.equals("")) {
			lore.add(plugin.cMsg(infoColour + infoDisplay + ":"));
			lore.addAll(plugin.wrapString(info));
		} else {
			lore.add(plugin.cMsg(infoColour + infoDisplay + ": &f" + info));
		}

		lore.add(plugin.cMsg(seriesColour + seriesDisplay + ": &f" + series));
		if (plugin.getCardsConfig().getConfig().contains("Cards." + rare + "." + cardName + ".About")) {
			lore.add(plugin.cMsg(aboutColour + aboutDisplay + ": &f" + about));
		}

		if (isShiny) {
			lore.add(plugin.cMsg(rarityColour + ChatColor.BOLD + plugin.getConfig().getString("General.Shiny-Name") + " " + rare));
		} else {
			lore.add(plugin.cMsg(rarityColour + ChatColor.BOLD + rare));
		}

		cmeta.setLore(lore);
		if (plugin.getConfig().getBoolean("General.Hide-Enchants", true)) {
			cmeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		card.setItemMeta(cmeta);
		if (isShiny) {
			card.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
		}

		return card;

	}
}
