package net.tinetwork.tradincards.tradincardsplugin.managers;

import net.tinetwork.tradincards.tradincardsplugin.utils.CardUtil;
import net.tinetwork.tradincards.tradincardsplugin.TradingCards;
import net.tinetwork.tradincards.tradincardsplugin.card.NullCard;
import net.tinetwork.tradincards.tradincardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradincards.tradincardsplugin.config.SimpleCardsConfig;
import net.tinetwork.tradincards.tradincardsplugin.config.TradingCardsConfig;
import net.tinetwork.tradingcards.api.manager.CardManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TradingCardManager extends CardManager<TradingCard> {
	private static TradingCards plugin;
	private static final Map<String, Card<TradingCard>> cards = new HashMap<>();
	private static final Map<String, Card<TradingCard>> activeCards = new HashMap<>();

	private static final Map<String, List<String>> rarityCardList = new HashMap<>();


	public static List<String> getRarityCardList(final String rarity) {
		return rarityCardList.get(rarity);
	}

	public static Set<String> getRarityNames() {
		return rarityCardList.keySet();
	}


	/**
	 * Pre-loads all existing cards.
	 */
	public static void init(final TradingCards plugin) {
		TradingCardManager.plugin = plugin;
		loadCards();
		plugin.getLogger().info(String.format("Loaded %d cards.",cards.size()));
		plugin.debug(StringUtils.join(cards.keySet(), ","));
	}



	private static void loadCards() {
		for(SimpleCardsConfig simpleCardsConfig: plugin.getCardsConfig().getCardConfigs()) {
			for(final String rarity: simpleCardsConfig.getCards().getKeys(false)) {
				rarityCardList.put(rarity,new ArrayList<>());
				for(String name: simpleCardsConfig.getCards().getConfigurationSection(rarity).getKeys(false)) {
					cards.put(rarity+"."+name, CardUtil.generateCard(simpleCardsConfig,name,rarity,false));
					rarityCardList.get(rarity).add(name);
					if(plugin.getMainConfig().activeSeries.contains(simpleCardsConfig.getSeries(rarity,name))) {
						activeCards.put(rarity+"."+name, cards.get(rarity+"."+name));
					}
				}
			}
		}
	}

	/*public static Map<String,TradingCard> getCards(){
		return cards;
	}*/

	@Override
	public Map<String, Card<TradingCard>> getCards() {
		return cards;
	}

	public Map<String, Card<TradingCard>> getActiveCards() {
		return activeCards;
	}


	public TradingCard getCard(final String cardName,final String rarity, final boolean forcedShiny){
		if(cards.containsKey(rarity+"."+cardName))
			return (TradingCard) cards.get(rarity+"."+cardName).isShiny(forcedShiny);
		return new NullCard(plugin);
	}

	public TradingCard getActiveCard(final String cardName,final String rarity, final boolean forcedShiny){
		if(activeCards.containsKey(rarity+"."+cardName))
			return (TradingCard) activeCards.get(rarity+"."+cardName);
		//fallthrough
		return (TradingCard) getCard(cardName,rarity,forcedShiny);
	}
	public TradingCard getRandomCard(final String rarity, final boolean forcedShiny) {
		var cindex = plugin.getRandom().nextInt(getRarityCardList(rarity).size());
		String randomCardName = getRarityCardList(rarity).get(cindex);
		return getCard(randomCardName, rarity, forcedShiny);
	}

	public TradingCard getRandomActiveCard(final String rarity, final boolean forcedShiny) {
		var cindex = plugin.getRandom().nextInt(activeCards.keySet().size());
		List<String> cardNames = getRarityCardList(rarity);
		String randomCardName = cardNames.get(cindex);
		return getActiveCard(randomCardName, rarity, forcedShiny);
	}

	@Override
	public ItemStack getCardItem(String cardName, String rarity, int num) {
		TradingCard card = (TradingCard) cards.get(rarity+"."+cardName);
		ItemStack cardItem = card.build();
		cardItem.setAmount(num);
		return cardItem;
	}

	public static ItemStack getCard(final String cardName,final String rarity, int num){
		TradingCard card = (TradingCard) cards.get(rarity+"."+cardName);
		ItemStack cardItem = card.build();
		cardItem.setAmount(num);
		return cardItem;
	}

	/**
	 * @deprecated Use PackManager.getPackItem(String name);
	 */
	@Deprecated
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
