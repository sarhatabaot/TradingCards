package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.chance.Chance;
import net.tinetwork.tradingcards.api.model.chance.EmptyChance;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import static net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil.cardKey;

public class TradingCardManager implements CardManager<TradingCard> {
    private final TradingCards plugin;
    public static final EmptyCard NULL_CARD = new EmptyCard();

    //CardKey,Card<TradingCard>
    //This should be entirely in storage
    private Map<String, TradingCard> cards;
    private List<String> activeCards;

    //This stores the cards from a single rarity, over multiple files
    //Should be under a specific storage
    private Map<String, List<String>> rarityCardMap;

    //active series rarity list see plugin.getStorage().getCardsInRarityAndSeries()
    private Map<String, List<String>> activeRarityCardMap;

    public TradingCardManager(final TradingCards plugin) {
        this.plugin = plugin;
        initValues();
    }


    public void initValues() {
        loadAllCards();
        loadActiveCards();
        plugin.getLogger().info(String.format("Loaded %d cards.", cards.size()));
        plugin.getLogger().info(String.format("Loaded %d rarities", rarityCardMap.keySet().size()));
        plugin.debug(TradingCardManager.class,StringUtils.join(rarityCardMap.keySet(), ","));
        plugin.debug(TradingCardManager.class,StringUtils.join(cards.keySet(), ","));
    }



    /**
     * Pre-loads all existing cards.
     */
    private void loadAllCards() {
        this.cards = plugin.getStorage().getCardsMap();
        this.activeCards = new ArrayList<>();
        loadRarityCardNames();
        loadActiveCards();
    }

    private void loadActiveCards() {
        loadActiveCardNames();
        loadActiveRarityCardListNames();
    }

    private void loadActiveCardNames() {
        for(TradingCard card: plugin.getStorage().getActiveCards()) {
            this.activeCards.add(cardKey(card.getRarity().getName(),card.getCardId()));
        }
    }

    private void loadActiveRarityCardListNames() {
        this.activeRarityCardMap = new HashMap<>();
        for(Series series: plugin.getStorage().getActiveSeries()) {
            for (Rarity rarity: plugin.getStorage().getRarities()) {
                activeRarityCardMap.putIfAbsent(rarity.getName(),new ArrayList<>());
                List<String> currentList = activeRarityCardMap.get(rarity.getName());
                Stream<String> cardIdInRarityAndSeries = plugin.getStorage().getCardsInRarityAndSeries(rarity.getName(),series.getName()).stream().map(TradingCard::getCardId);
                List<String> mergedList = Stream.concat(currentList.stream(),cardIdInRarityAndSeries).toList();
                activeRarityCardMap.put(rarity.getName(),mergedList);
            }

        }
    }

    private void loadRarityCardNames(){
        this.rarityCardMap = new HashMap<>();
        for(Rarity rarity: plugin.getStorage().getRarities()) {
            rarityCardMap.put(rarity.getName(),plugin.getStorage().getCardsInRarity(rarity.getName()).stream().map(TradingCard::getCardId).toList());
        }
    }

    @Override
    @Nullable
    public List<TradingCard> getRarityCardList(final String rarity) {
        return plugin.getStorage().getCardsInRarity(rarity);
    }

    @Override
    public List<String> getRarityCardListNames(final String rarity) {
        return this.rarityCardMap.get(rarity);
    }

    @Override
    public List<String> getActiveRarityCardList(final String rarity) {
        return this.activeRarityCardMap.get(rarity);
    }

    @Override
    public Set<String> getRarityNames() {
        return rarityCardMap.keySet();
    }

    @Override
    public Set<String> getActiveRarityNames() {
        return activeRarityCardMap.keySet();
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
        return NULL_CARD;
    }

    public TradingCard getCard(final String cardId, final String rarityId) {
        final String cardKey = cardKey(rarityId,cardId);
        plugin.debug(TradingCardManager.class,"CardKey="+cardKey);
        if (cards.containsKey(cardKey)) {
            return cards.get(cardKey).get();
        }
        return NULL_CARD;
    }

    @Override
    public TradingCard getActiveCard(final String cardName, final String rarity, final boolean forcedShiny) {
        final String cardKey = cardKey(rarity,cardName);
        if (activeCards.contains(cardKey)) {
            return getCard(cardName,rarity,forcedShiny);
        }
        //fallthrough
        //if it doesn't contain this card for some reason
        return NULL_CARD;
    }

    @Override
    public TradingCard getActiveCard(final String cardName, final String rarity) {
        final String cardKey = cardKey(rarity,cardName);
        if (activeCards.contains(cardKey)) {
            return getCard(cardName,rarity);
        }
        //fallthrough
        //if it doesn't contain this card for some reason
        return NULL_CARD;
    }

    @Override
    public TradingCard getRandomCard(final String rarity) {
        plugin.debug(TradingCardManager.class,"getRandomCard(),rarity=" + rarity);
        var cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarity).size());
        String randomCardName = getRarityCardList(rarity).get(cardIndex).getCardId();
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
        String randomCardName = getRarityCardList(rarity).get(cardIndex).getCardId();
        return getCard(randomCardName, rarity, forcedShiny);
    }


    public TradingCard getRandomCard(final String rarity, final String series) {
        return getRandomCard(rarity,series,false);
    }

    public TradingCard getRandomCard(final String rarity, final String series, final boolean forcedShiny) {
        if(series == null)
            return getRandomCard(rarity,forcedShiny);

        List<TradingCard> raritySeries = plugin.getStorage().getCardsInRarityAndSeries(rarity,series);
        int cardIndex = plugin.getRandom().nextInt(raritySeries.size());
        return raritySeries.get(cardIndex);
    }

    @Override
    public TradingCard getRandomActiveCard(final String rarity, final boolean forcedShiny) {
        if (activeCards.isEmpty()) {
            plugin.debug(TradingCardManager.class,"There are no cards in the active series. Not dropping anything.");
            return NULL_CARD;
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


    public boolean containsCard(final String cardId, final String rarityId, final String seriesId) {
        if(cards.containsKey(cardKey(rarityId,cardId))) {
            TradingCard card = cards.get(cardKey(rarityId,cardId));
            return card.getSeries().getName().equals(seriesId);
        }
        return false;
    }
}
