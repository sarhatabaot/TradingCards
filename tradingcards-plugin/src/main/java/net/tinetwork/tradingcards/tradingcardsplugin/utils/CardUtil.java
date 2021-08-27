package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import de.tr7zw.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.model.MobType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
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
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
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
	public static final int RANDOM_MAX = 100000;
	public static ItemStack BLANK_CARD;

	private static final String PLACEHOLDER_PREFIX = "%PREFIX%";
	private static final String PLACEHOLDER_COLOR = "%COLOR%";
	private static final String PLACEHOLDER_NAME ="%NAME%";
	private static final String PLACEHOLDER_BUY_PRICE = "%BUY_PRICE%";
	private static final String PLACEHOLDER_SELL_PRICE = "%SELL_PRICE%";
	private static final  String PLACEHOLDER_SHINY_PREFIX = "%SHINY_PREFIX";

	public static void init(final TradingCards plugin) {
		CardUtil.plugin = plugin;
		CardUtil.cardManager = plugin.getCardManager();
		CardUtil.BLANK_CARD = plugin.getGeneralConfig().blankCard();
	}

	public static String getRarityName(@NotNull final String rarity) {
		return rarity.replace(stripAllColor(plugin.getGeneralConfig().shinyName()), "").trim();
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


	/**
	 * Returns the rarity that should drop.
	 *
	 * @param e
	 * @param alwaysDrop
	 * @return String Rarity that should drop
	 */
	@NotNull
	public static String calculateRarity(EntityType e, boolean alwaysDrop) {
		int randomChance = plugin.getRandom().nextInt(RANDOM_MAX) + 1;
		for(Rarity rarity: plugin.getRaritiesConfig().rarities()) {
			var chance = plugin.getChancesConfig().getChance(rarity.getName()).getFromMobType(CardUtil.getMobType(e));
			if(alwaysDrop || randomChance < chance)
				return rarity.getName();
		}

		return "None";
	}


	public static boolean isCard(final ItemStack itemStack) {
		if(!isCardMaterial(itemStack.getType()))
			return false;

		NBTItem nbtItem = new NBTItem(itemStack);
		return nbtItem.getBoolean("isCard");
	}

	private static boolean isCardMaterial(final  Material material) {
		return material == plugin.getGeneralConfig().cardMaterial();
	}

	private static void broadcastPrefixedMessage(final String message) {
		Bukkit.broadcastMessage(plugin.getPrefixedMessage(message));
	}

	public static void giveawayNatural(EntityType mob, Player sender) {
		if (plugin.isMobBoss(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBossNoPlayer());
			} else {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalBoss().replace("%player%", sender.getName()));
			}
		} else if (plugin.isMobHostile(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostileNoPlayer());
			} else {
				broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalHostile().replace("%player%", sender.getName()));
			}
		} else if (plugin.isMobNeutral(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutralNoPlayer()));
			} else {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNeutral().replace("%player%", sender.getName())));
			}
		} else if (plugin.isMobPassive(mob)) {
			if (sender == null) {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassiveNoPlayer()));
			} else {
				broadcastPrefixedMessage(plugin.getPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalPassive().replace("%player%", sender.getName())));
			}
		} else if (sender == null) {
			broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNaturalNoPlayer());
		} else {
			broadcastPrefixedMessage(plugin.getMessagesConfig().giveawayNatural().replace("%player%", sender.getName()));
		}

		for (final Player p : Bukkit.getOnlinePlayers()) {
			String rare = cardManager.getRandomRarity(mob, true);
			plugin.debug("onCommand.rare: " + rare);
			CardUtil.dropItem(p, cardManager.getRandomCard(rare, false).build());
		}

	}
	public static String formatDisplayName(final TradingCard card) {
		final String[] shinyPlayerCardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE,PLACEHOLDER_SELL_PRICE, PLACEHOLDER_SHINY_PREFIX};
		final String[] shinyCardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE,PLACEHOLDER_SELL_PRICE, PLACEHOLDER_SHINY_PREFIX, "_"};

		final String[] cardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE, PLACEHOLDER_SELL_PRICE,"_"};
		final String[] playerCardFormat = new String[]{PLACEHOLDER_PREFIX, PLACEHOLDER_COLOR, PLACEHOLDER_NAME, PLACEHOLDER_BUY_PRICE, PLACEHOLDER_SELL_PRICE};

		Rarity rarity;
		try {
			rarity = plugin.getRaritiesConfig().getRarity(card.getRarity());
		} catch (SerializationException e){
			plugin.getLogger().severe(e.getMessage());
			return null;
		}
		final String shinyTitle = plugin.getGeneralConfig().displayShinyTitle();
		final String title = plugin.getGeneralConfig().displayTitle();
		final String shinyPrefix = plugin.getGeneralConfig().shinyName();
		final String prefix = plugin.getGeneralConfig().cardPrefix();
		final String rarityColour = rarity.getDefaultColor();
		final String buyPrice = String.valueOf(card.getBuyPrice());
		final String sellPrice =String.valueOf(card.getSellPrice());

		if (card.isShiny() && shinyPrefix != null) {
			if (card.isPlayerCard()) {
				return ChatUtil.color(StringUtils.replaceEach(shinyTitle, shinyPlayerCardFormat, new String[]{prefix, rarityColour,card.getDisplayName(), buyPrice,sellPrice, shinyPrefix}));
			}
			return ChatUtil.color(StringUtils.replaceEach(shinyTitle, shinyCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice,sellPrice, shinyPrefix, " "}));
		}
		if (card.isPlayerCard()) {
			return ChatUtil.color(StringUtils.replaceEach(title, playerCardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice,sellPrice}));
		}
		return ChatUtil.color(StringUtils.replaceEach(title, cardFormat, new String[]{prefix, rarityColour, card.getDisplayName(), buyPrice,sellPrice, " "}));
	}

	public static List<String> formatLore(final String info, final String about, final String rarity, final boolean isShiny, final String type, final String series) {
		List<String> lore = new ArrayList<>();
		final String typeFormat = ChatUtil.color(plugin.getGeneralConfig().colorType() + plugin.getGeneralConfig().displayType() + ": &f" + type);
		final String infoFormat = ChatUtil.color(plugin.getGeneralConfig().colorInfo() + plugin.getGeneralConfig().displayInfo() + ": &f");
		final String seriesFormat = ChatUtil.color(plugin.getGeneralConfig().colorSeries()+ plugin.getGeneralConfig().displaySeries() + ": &f" + series);
		final String aboutFormat = ChatUtil.color(plugin.getGeneralConfig().colorAbout()+ plugin.getGeneralConfig().displayAbout() + ": &f");
		final String rarityFormat = ChatUtil.color(plugin.getGeneralConfig().colorRarity() + ChatColor.BOLD);

		lore.add(typeFormat);
		if (!"None".equalsIgnoreCase(info) && !info.isEmpty()) {
			lore.add(infoFormat);
			lore.addAll(ChatUtil.wrapString(info));
		} else {
			lore.add(infoFormat + info);
		}

		lore.add(seriesFormat);
		if (about!=null) {
			lore.add(aboutFormat + about);
		}

		final String rarityName = rarity.replace('_', ' ');
		if (isShiny) {
			lore.add(rarityFormat + plugin.getGeneralConfig().shinyName() + " " + rarityName);
		} else {
			lore.add(rarityFormat + rarityName);
		}

		return lore;
	}

}
