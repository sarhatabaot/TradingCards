package media.xen.tradingcards;

import media.xen.tradingcards.api.card.TradingCard;
import media.xen.tradingcards.config.TradingCardsConfig;
import org.apache.commons.lang.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardManager {
	private static TradingCards plugin;
	private static final Map<String, TradingCard> cards = new HashMap<>();
	private static final Map<String, TradingCard> activeCards = new HashMap<>();

	/**
	 * Pre-loads all existing cards.
	 */
	public static void init(final TradingCards plugin) {
		CardManager.plugin = plugin;
		for(String rarity: plugin.getCardsConfig().getConfig().getConfigurationSection("Cards").getKeys(false)){
			for(String name: plugin.getCardsConfig().getConfig().getConfigurationSection("Cards."+rarity).getKeys(false)) {
				cards.put(rarity+"."+name, CardUtil.generateCard(name,rarity,false));
				final String series = plugin.getCardsConfig().getConfig().getString("Cards."+rarity+"."+name+".series");
				if(plugin.getMainConfig().activeSeries.contains(series)) {
					activeCards.put(rarity + "." + name, cards.get(rarity + "." + name));
				}
			}
		}

		plugin.getLogger().info(String.format("Loaded %d cards.",cards.size()));
		plugin.debug(StringUtils.join(cards.keySet(), ","));
	}

	public static Map<String,TradingCard> getCards(){
		return cards;
	}

	public static Map<String, TradingCard> getActiveCards() {
		return activeCards;
	}

	/**
	 *
	 * @param cardName
	 * @param rarity
	 * @return false if cards already exists. True if successfully added.
	 */
	public static boolean addCard(final String cardName, final String rarity){
		if(cards.containsKey(rarity+"."+cardName))
			return false;
		cards.put(rarity+"."+cardName, CardUtil.generateCard(cardName,rarity,false));
		return true;
	}

	public static TradingCard getCard(final String cardName,final String rarity, final boolean forcedShiny){
		if(cards.containsKey(rarity+"."+cardName))
			return cards.get(rarity+"."+cardName);
		return CardUtil.generateCard(cardName,rarity,forcedShiny);
	}

	public static TradingCard getActiveCard(final String cardName,final String rarity, final boolean forcedShiny){
		if(activeCards.containsKey(rarity+"."+cardName))
			return activeCards.get(rarity+"."+cardName);
		//fallthrough
		return getCard(cardName,rarity,forcedShiny);
	}



	public static ItemStack getCard(final String cardName,final String rarity, int num){
		TradingCard card = cards.get(rarity+"."+cardName);
		ItemStack cardItem = card.build();
		cardItem.setAmount(num);
		return cardItem;
	}

	public static ItemStack generatePack(final String name) {
		ItemStack boosterPack = TradingCardsConfig.getBlankBoosterPack();
		int numNormalCards = plugin.getConfig().getInt("BoosterPacks." + name + ".NumNormalCards");
		int numSpecialCards = plugin.getConfig().getInt("BoosterPacks." + name + ".NumSpecialCards");
		String prefix = plugin.getMainConfig().boosterPackPrefix;
		String normalCardColour = plugin.getConfig().getString("Colours.BoosterPackNormalCards");
		String extraCardColour = plugin.getConfig().getString("Colours.BoosterPackExtraCards");
		String loreColour = plugin.getMainConfig().boosterPackLoreColour;
		String nameColour = plugin.getMainConfig().boosterPackNameColour;
		String normalRarity = plugin.getConfig().getString("BoosterPacks." + name + ".NormalCardRarity");
		String specialRarity = plugin.getConfig().getString("BoosterPacks." + name + ".SpecialCardRarity");
		String extraRarity = "";
		int numExtraCards = 0;
		boolean hasExtraRarity = false;
		if (plugin.getConfig().contains("BoosterPacks." + name + ".ExtraCardRarity") && plugin.getConfig().contains("BoosterPacks." + name + ".NumExtraCards")) {
			hasExtraRarity = true;
			extraRarity = plugin.getConfig().getString("BoosterPacks." + name + ".ExtraCardRarity");
			numExtraCards = plugin.getConfig().getInt("BoosterPacks." + name + ".NumExtraCards");
		}

		String specialCardColour = plugin.getConfig().getString("Colours.BoosterPackSpecialCards");
		ItemMeta pMeta = boosterPack.getItemMeta();
		pMeta.setDisplayName(plugin.cMsg(prefix + nameColour + name.replace("_", " ")));
		List<String> lore = new ArrayList<>();
		lore.add(plugin.cMsg(normalCardColour + numNormalCards + loreColour + " " + normalRarity.toUpperCase()));
		if (hasExtraRarity) {
			lore.add(plugin.cMsg(extraCardColour + numExtraCards + loreColour + " " + extraRarity.toUpperCase()));
		}

		lore.add(plugin.cMsg(specialCardColour + numSpecialCards + loreColour + " " + specialRarity.toUpperCase()));
		pMeta.setLore(lore);
		if (plugin.getMainConfig().hideEnchants) {
			pMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}

		boosterPack.setItemMeta(pMeta);
		boosterPack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 10);
		return boosterPack;
	}


}
