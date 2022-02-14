package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.card.Card;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.chance.Chance;
import net.tinetwork.tradingcards.api.model.chance.EmptyChance;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.config.SimpleCardsConfig;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil.cardKey;

public class TradingCardManager implements CardManager<TradingCard> {
    private final TradingCards plugin;
    public static final EmptyCard NULL_CARD = new EmptyCard();

    //CardKey,Card<TradingCard>
    private Map<String, TradingCard> cards;
    private List<String> activeCards;

    //This stores the cards from a single rarity, over multiple files
    private Map<String, List<String>> rarityCardList;
    private Map<String, List<String>> activeRarityCardList;


    public TradingCardManager(final TradingCards plugin) {
        this.plugin = plugin;
        initValues();
    }


    public void initValues() {
        loadAllCards();
        loadActiveCards();
        plugin.getLogger().info(String.format("Loaded %d cards.", cards.size()));
        plugin.getLogger().info(String.format("Loaded %d rarities", rarityCardList.keySet().size()));
        plugin.debug(TradingCardManager.class,StringUtils.join(rarityCardList.keySet(), ","));
        plugin.debug(TradingCardManager.class,StringUtils.join(cards.keySet(), ","));
    }


    /**
     * Pre-loads all existing cards.
     */
    //TODO should be done in specific storage impl
    private void loadAllCards() {
        this.rarityCardList = new HashMap<>();
        this.cards = plugin.getStorage().getCardsMap();
        this.activeCards = new ArrayList<>();
        loadActiveCards();
    }

    //TODO should be done in specific storage impl
    // This can be done in SQL fairly easily..
    private void loadActiveCards() {
        loadActiveCardNames();
        loadActiveRarityCardListNames();
    }

    private void loadActiveCardNames() {
        for(TradingCard card: plugin.getStorage().getActiveCards()) {
            this.activeCards.add(cardKey(card.getRarity().getName(),card.getCardName()));
        }
    }

    private void loadActiveRarityCardListNames() {
        this.activeRarityCardList = new HashMap<>();
        for(TradingCard card: plugin.getStorage().getActiveCards()) {
            final Rarity rarity = card.getRarity();
            activeRarityCardList.putIfAbsent(rarity.getName(),new ArrayList<>());
            activeRarityCardList.get(rarity.getName()).add(card.getCardName());
        }
    }

    @Override
    public List<TradingCard> getRarityCardList(final String rarity) {
        return plugin.getStorage().getCardsInRarity(rarity);
    }

    @Override
    public List<String> getRarityCardListNames(final String rarity) {
        return this.rarityCardList.get(rarity);
    }

    @Override
    public List<String> getActiveRarityCardList(final String rarity) {
        return this.activeRarityCardList.get(rarity);
    }

    @Override
    public Set<String> getRarityNames() {
        return rarityCardList.keySet();
    }

    @Override
    public Set<String> getActiveRarityNames() {
        return activeRarityCardList.keySet();
    }


    @Override
    public Map<String, TradingCard> getCards() {
        return cards;
    }


    @Override
    public List<String> getActiveCards() {
        return activeCards;
    }

    @Override
    public TradingCard getCard(final String cardName, final String rarity, final boolean forcedShiny) {
        if (cards.containsKey(cardKey(rarity,cardName))) {
            TradingCard card = cards.get(cardKey(rarity, cardName)).get();
            if(card.hasShiny())
                card.isShiny(forcedShiny);
            return card;
        }
        return new EmptyCard();
    }

    public TradingCard getCard(final String cardName, final String rarity) {
        if (cards.containsKey(cardKey(rarity,cardName))) {
            return cards.get(cardKey(rarity, cardName)).get();
        }
        return new EmptyCard();
    }

    @Override
    public TradingCard getActiveCard(final String cardName, final String rarity, final boolean forcedShiny) {
        final String cardKey = cardKey(rarity,cardName);
        if (activeCards.contains(cardKey)) {
            return getCard(cardName,rarity,forcedShiny);
        }
        //fallthrough
        //if it doesn't contain this card for some reason
        return new EmptyCard();
    }

    @Override
    public TradingCard getActiveCard(final String cardName, final String rarity) {
        final String cardKey = cardKey(rarity,cardName);
        if (activeCards.contains(cardKey)) {
            return getCard(cardName,rarity);
        }
        //fallthrough
        //if it doesn't contain this card for some reason
        return new EmptyCard();
    }

    @Override
    public TradingCard getRandomCard(final String rarity) {
        plugin.debug(TradingCardManager.class,"getRandomCard(),rarity=" + rarity);
        var cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarity).size());
        String randomCardName = getRarityCardList(rarity).get(cardIndex).getCardName();
        return getCard(randomCardName, rarity);
    }

    @Override
    public TradingCard getRandomActiveCard(final String rarity) {
        return getRandomActiveCard(rarity,false);
    }

    @Override
    public TradingCard getRandomCard(final String rarity, final boolean forcedShiny) {
        plugin.debug(TradingCardManager.class,"getRandomCard(),rarity=" + rarity);
        var cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarity).size());
        String randomCardName = getRarityCardList(rarity).get(cardIndex).getCardName();
        return getCard(randomCardName, rarity, forcedShiny);
    }


    public TradingCard getRandomCard(final String rarity, final String series) {
        return getRandomCard(rarity,series,false);
    }

    public TradingCard getRandomCard(final String rarity, final String series, final boolean forcedShiny) {
        if(series == null)
            return getRandomCard(rarity,forcedShiny);
        //TODO Reallly inefficient
        List<String> raritySeries = new ArrayList<>();
        for(String cardKey: getRarityCardListNames(rarity)) {
            TradingCard tradingCard = getCard(cardKey,rarity,forcedShiny);
            if(tradingCard.getSeries().getName().equals(series)) {
                raritySeries.add(tradingCard.getCardName());
            }
        }
        var cindex = plugin.getRandom().nextInt(raritySeries.size());
        String randomCardName = raritySeries.get(cindex);
        return getCard(randomCardName, rarity, forcedShiny);
    }

    @Override
    public TradingCard getRandomActiveCard(final String rarity, final boolean forcedShiny) {
        if (activeCards.isEmpty()) {
            plugin.debug(TradingCardManager.class,"There are no cards in the active series. Not dropping anything.");
            return new EmptyCard();
        }

        List<String> cardNames = getActiveRarityCardList(rarity);
        var cardIndex = plugin.getRandom().nextInt(cardNames.size());
        String randomCardName = cardNames.get(cardIndex);
        return getActiveCard(randomCardName, rarity, forcedShiny);
    }

    private int getGeneralMobChance(@NotNull DropType dropType) {
        return switch (dropType.getType()) {
            case "boss" -> plugin.getChancesConfig().bossChance();
            case "hostile" -> plugin.getChancesConfig().hostileChance();
            case "neutral" -> plugin.getChancesConfig().neutralChance();
            case "passive" -> plugin.getChancesConfig().passiveChance();
            default -> 0;
        };
    }

    public String getRandomRarity(DropType dropType, boolean alwaysDrop) {
        int randomDropChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        int mobDropChance = getGeneralMobChance(dropType);
        plugin.debug(TradingCardManager.class,"DropChance=" + randomDropChance +" AlwaysDrop=" + alwaysDrop +" MobType="+ dropType +" MobDropChance="+mobDropChance);
        if (!alwaysDrop && randomDropChance > mobDropChance) {
            return "None";
        }

        int randomRarityChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        plugin.debug(TradingCardManager.class,"RarityChance=" + randomRarityChance);

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getCardManager().getRarityNames());
        for (String rarity : rarityKeys.descendingSet()) {
            Chance chance = plugin.getChancesConfig().getChance(rarity);
            if (chance instanceof EmptyChance)
                return "None";

            int chanceInt = chance.getFromMobType(dropType);
            if (randomRarityChance < chanceInt)
                return rarity;
        }
        return "None";
    }


    public TradingCard generateCard(final SimpleCardsConfig simpleCardsConfig, final String cardId, final String rarityId) {
        if ("none".equalsIgnoreCase(rarityId)) {
            return NULL_CARD;
        }

        return simpleCardsConfig.getCard(rarityId, cardId).get();
    }


}
