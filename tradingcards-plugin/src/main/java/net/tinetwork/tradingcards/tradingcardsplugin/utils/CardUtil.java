package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.NullCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.SimpleCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingCardManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * @author sarhatabaot
 */
public class CardUtil {
	private static TradingCards plugin;
	private static TradingCardManager cardManager;
	private static final char ALT_COLOR_CHAR = '&';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + ALT_COLOR_CHAR + "[0-9A-FK-ORX]");
	private static final String NAME_TEMPLATE = "^[a-zA-Z0-9-_]+$";

	public static void init(final TradingCards plugin) {
		CardUtil.plugin = plugin;
		CardUtil.cardManager = plugin.getCardManager();
	}

	public static String getRarityName(@NotNull final String rarity) {
		return rarity.replace(stripAllColor(plugin.getMainConfig().shinyName), "").trim();
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
		int random = plugin.getRandom().nextInt(100000) + 1;
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

	private static boolean rarityExists(Set<String> rarityKeys, String rarity){
		for (String type2 : rarityKeys) {
			if (type2.equalsIgnoreCase(rarity)) {
				return true;
			}
		}
		return false;
	}

	//returns "None" if it doesn't match.
	private static String getCorrectNameTemplate(String string) {
		if(string.matches(NAME_TEMPLATE)){
			return string;
		}
		return "None";
	}

	@NotNull
	@Deprecated
	public static TradingCard getRandomCard(@NotNull final String rarityName, final boolean forcedShiny) {
		return cardManager.getRandomCard(rarityName,forcedShiny);
	}



	@NotNull
	@Deprecated
	public static TradingCard getRandomActiveCard(@NotNull final String rarityName, final boolean forcedShiny) {
		return cardManager.getRandomActiveCard(rarityName,forcedShiny);
	}

	/**
	 * Strips the given message of all color codes
	 *
	 * @param input String to strip of color
	 * @return A copy of the input string, without any coloring
	 */
	@Contract("!null -> !null; null -> null")
	@Nullable
	public static String stripAllColor(@Nullable final String input) {
		if (input == null) {
			return null;
		}

		return ChatColor.stripColor(STRIP_COLOR_PATTERN.matcher(input).replaceAll(""));
	}

	@NotNull
	public static String getCardName(@NotNull final String displayRarity, @NotNull final String displayCard) {
		final String strippedRarity = getRarityName(displayRarity);
		final boolean hasPrefix = plugin.getMainConfig().cardPrefix != null || !plugin.getMainConfig().cardPrefix.equals("");
		final String strippedPrefix = stripAllColor(plugin.getMainConfig().cardPrefix);
		final String strippedShiny = stripAllColor(plugin.getMainConfig().shinyName);
		final String strippedDisplay = StringUtils.replaceEach(stripAllColor(displayCard), new String[]{strippedPrefix, strippedShiny}, new String[]{"", ""}).trim();
		plugin.debug("stripped|rarity=" + strippedRarity + "|hasPrefix=" + hasPrefix + "|prefix=" + strippedPrefix + "|shiny=" + strippedShiny + "|display=" + strippedDisplay);

		if (cardManager.getCard(strippedDisplay,strippedRarity,false).getCardName().equals("nullCard")) {
			plugin.debug("No such card. card=" + strippedDisplay + "rarity=" + strippedRarity);
			return "None";
		}

		if (cardManager.getCards().keySet().contains(strippedRarity+"."+strippedDisplay.replace(" ","_")))
			return strippedDisplay.replace(" ","_");

		return "None";
	}

	public enum MobType {
		HOSTILE,
		NEUTRAL,
		PASSIVE,
		BOSS
	}

	public static MobType getMobType(EntityType e) {
		if (plugin.isMobHostile(e)) {
			return MobType.HOSTILE;
		}
		if (plugin.isMobNeutral(e)) {
			return MobType.NEUTRAL;
		}
		if(plugin.isMobPassive(e)) {
			return MobType.PASSIVE;
		}
		return MobType.BOSS;
	}


	@NotNull
	//TODO
	//This should not calculate a rarity, but rather just return the mobtype
	public static String getMobTypeOrNone(EntityType e, boolean alwaysDrop) {
		int generatedDropChance = plugin.getRandom().nextInt(100000) + 1;
		plugin.debug("shouldItDrop Num: " + generatedDropChance);
		if (plugin.isMobHostile(e)) {
			if (!alwaysDrop && generatedDropChance > plugin.getMainConfig().hostileChance) {
				return "None";
			}

			return "Hostile";
		}
		if (plugin.isMobNeutral(e)) {
			if (!alwaysDrop && generatedDropChance > plugin.getMainConfig().neutralChance) {
				return "None";
			}
			return "Neutral";
		}
		if (plugin.isMobPassive(e)) {
			if (!alwaysDrop && generatedDropChance > plugin.getMainConfig().passiveChance) {
				return "None";

			}
			return "Passive";
		}

		if (!plugin.isMobBoss(e)) {
			if (!alwaysDrop && generatedDropChance > plugin.getMainConfig().bossChance) {
				return "None";
			}
			return "Boss";

		}

		return "None";
	}

	/**
	 * Returns the rarity that should drop.
	 *
	 * @param e
	 * @param alwaysDrop
	 * @return String Rarity that should drop
	 */
	@NotNull
	public static String calculateRarity(EntityType e, boolean alwaysDrop) {
		String mobType = getMobTypeOrNone(e, alwaysDrop);
		if(mobType.equalsIgnoreCase("None"))
			return "None";

		int randomChance = plugin.getRandom().nextInt(100000) + 1;
		TreeSet<String> rarityKeys = new TreeSet<>(plugin.getMainConfig().rarities().getKeys(false));
		for(String rarity: rarityKeys.descendingSet()) {
			var chance = plugin.getConfig().getInt("Chances." + rarity + "." + mobType, -1);
			if(randomChance < chance)
				return rarity;
		}
		return "None";
	}

	public static boolean isPlayerCard(String name) {
		String rarity = plugin.getConfig().getString("General.Auto-Add-Player-Rarity");
		return !cardManager.getCard(name,rarity,false).getCardName().equals("nullCard") && cardManager.getCard(name,rarity,false).isPlayerCard();
	}

	public static boolean isCard(final ItemStack itemStack) {
		if(!isCardMaterial(itemStack.getType()))
			return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getBoolean("isCard");
	}

	private static boolean isCardMaterial(final  Material material) {
		return material == Material.valueOf(plugin.getMainConfig().cardMaterial);
	}


	public static class NotACardException extends Exception {
		private final long serial = 1L;

		public NotACardException(final String message) {
			super(message);
		}
	}



	private static void broadcastPrefixedMessage(final String message) {
		Bukkit.broadcastMessage(plugin.getPrefixedMessage(message));
	}

	public static void giveawayNatural(EntityType mob, Player sender) {
		if (plugin.isMobBoss(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBossNoPlayer);
			} else {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBoss.replaceAll("%player%", sender.getName()));
			}
		} else if (plugin.isMobHostile(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostileNoPlayer);
			} else {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostile.replaceAll("%player%", sender.getName()));
			}
		} else if (plugin.isMobNeutral(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutralNoPlayer));
			} else {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutral.replaceAll("%player%", sender.getName())));
			}
		} else if (plugin.isMobPassive(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassiveNoPlayer));
			} else {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassive.replaceAll("%player%", sender.getName())));
			}
		} else if (sender == null) {
			broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNoPlayer);
		} else {
			broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNatural.replaceAll("%player%", sender.getName()));
		}

		for (final Player p : Bukkit.getOnlinePlayers()) {
			String rare = cardManager.getRandomRarity(mob, true);
			plugin.debug("onCommand.rare: " + rare);
			CardUtil.dropItem(p, cardManager.getRandomCard(rare, false).build());
		}

	}


}
