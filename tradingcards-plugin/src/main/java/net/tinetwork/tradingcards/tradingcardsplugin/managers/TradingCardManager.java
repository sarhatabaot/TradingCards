package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.NullCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.SimpleCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.manager.CardManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TradingCardManager implements CardManager<TradingCard> {
    private final TradingCards plugin;
    private final Map<String, Card<TradingCard>> cards = new HashMap<>();
    private final Map<String, Card<TradingCard>> activeCards = new HashMap<>();

    private static final Map<String, List<String>> rarityCardList = new HashMap<>();



    public TradingCardManager(final TradingCards plugin) {
        this.plugin = plugin;
        loadCards();
        plugin.getLogger().info(String.format("Loaded %d cards.", cards.size()));
        plugin.debug(StringUtils.join(cards.keySet(), ","));
    }


    /**
     * Pre-loads all existing cards.
     */
    private void loadCards() {
        for (SimpleCardsConfig simpleCardsConfig : plugin.getCardsConfig().getCardConfigs()) {
            for (final String rarity : simpleCardsConfig.getCards().getKeys(false)) {
                rarityCardList.put(rarity, new ArrayList<>());
                for (String name : simpleCardsConfig.getCards().getConfigurationSection(rarity).getKeys(false)) {
                    cards.put(rarity + "." + name, CardUtil.generateCard(simpleCardsConfig, name, rarity, false));
                    rarityCardList.get(rarity).add(name);
                    if (plugin.getMainConfig().activeSeries.contains(simpleCardsConfig.getSeries(rarity, name))) {
                        activeCards.put(rarity + "." + name, cards.get(rarity + "." + name));
                    }
                }
            }
        }
    }

    @Override
    public List<String> getRarityCardList(final String rarity) {
        return rarityCardList.get(rarity);
    }
    @Override
    public Set<String> getRarityNames() {
        return rarityCardList.keySet();
    }


    @Override
    public Map<String, Card<TradingCard>> getCards() {
        return cards;
    }


    @Override
    public Map<String, Card<TradingCard>> getActiveCards() {
        return activeCards;
    }

    @Override
    public TradingCard getCard(final String cardName, final String rarity, final boolean forcedShiny) {
        if (cards.containsKey(rarity + "." + cardName))
            return (TradingCard) cards.get(rarity + "." + cardName).isShiny(forcedShiny);
        return new NullCard(plugin);
    }

    @Override
    public TradingCard getActiveCard(final String cardName, final String rarity, final boolean forcedShiny) {
        if (activeCards.containsKey(rarity + "." + cardName))
            return (TradingCard) activeCards.get(rarity + "." + cardName);
        //fallthrough
        return getCard(cardName, rarity, forcedShiny);
    }

    @Override
    public TradingCard getRandomCard(final String rarity, final boolean forcedShiny) {
        var cindex = plugin.getRandom().nextInt(getRarityCardList(rarity).size());
        String randomCardName = getRarityCardList(rarity).get(cindex);
        return getCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public TradingCard getRandomActiveCard(final String rarity, final boolean forcedShiny) {
        var cindex = plugin.getRandom().nextInt(activeCards.keySet().size());
        List<String> cardNames = getRarityCardList(rarity);
        String randomCardName = cardNames.get(cindex);
        return getActiveCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public ItemStack getCardItem(String cardName, String rarity, int num) {
        TradingCard card = (TradingCard) cards.get(rarity + "." + cardName);
        ItemStack cardItem = card.build();
        cardItem.setAmount(num);
        return cardItem;
    }

}
