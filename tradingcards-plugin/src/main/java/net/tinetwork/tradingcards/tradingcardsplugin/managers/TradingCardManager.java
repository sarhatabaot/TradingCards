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

    //This stores the ids of cards from a single rarity, over multiple files
    //rarity, card-list-id
    private Map<String, List<String>> rarityCardMap;

    //active series rarity list see plugin.getStorage().getCardsInRarityAndSeries()
    //rarity, card-list-id
    private Map<String, List<String>> activeRarityCardMap;

    private Map<String, Map<String, List<String>>> cardsInRarityAndSeriesIds;

    public TradingCardManager(final TradingCards plugin) {
        this.plugin = plugin;
        initValues();
    }

    public List<String> getCardsInRarityAndSeriesIds(final String rarityId, final String seriesId) {
        return this.cardsInRarityAndSeriesIds.get(rarityId).get(seriesId);
    }

    public void initValues() {
        loadAllCards();
        loadActiveCards();
        loadCardsInRaritiesAndSeriesIds();
        plugin.getLogger().info(() -> String.format("Loaded %d cards.", cards.size()));
        plugin.getLogger().info(() -> String.format("Loaded %d rarities", rarityCardMap.keySet().size()));
        plugin.debug(TradingCardManager.class,StringUtils.join(rarityCardMap.keySet(), ","));
        plugin.debug(TradingCardManager.class,StringUtils.join(cards.keySet(), ","));
    }

    private void loadCardsInRaritiesAndSeriesIds() {
        cardsInRarityAndSeriesIds = new HashMap<>();
        for(String rarityId: plugin.getRarityManager().getRarityIds()) {
            cardsInRarityAndSeriesIds.putIfAbsent(rarityId, new HashMap<>());
            for(String seriesId: plugin.getSeriesManager().getSeriesIds()) {
                cardsInRarityAndSeriesIds.get(rarityId).putIfAbsent(seriesId,new ArrayList<>());
                Stream<String> cardIdInRarityAndSeries = plugin.getStorage().getCardsInRarityAndSeries(rarityId,seriesId).stream().map(TradingCard::getCardId);
                cardsInRarityAndSeriesIds.get(rarityId).get(seriesId).addAll(cardIdInRarityAndSeries.toList());
            }
        }

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
            this.activeCards.add(cardKey(card.getRarity().getId(),card.getCardId()));
        }
    }

    private void loadActiveRarityCardListNames() {
        this.activeRarityCardMap = new HashMap<>();
        for(Series series: plugin.getStorage().getActiveSeries()) {
            for (Rarity rarity: plugin.getRarityManager().getRarities()) {
                activeRarityCardMap.putIfAbsent(rarity.getId(),new ArrayList<>());
                List<String> currentList = activeRarityCardMap.get(rarity.getId());
                Stream<String> cardIdInRarityAndSeries = plugin.getStorage().getCardsInRarityAndSeries(rarity.getId(),series.getId()).stream().map(TradingCard::getCardId);
                List<String> mergedList = Stream.concat(currentList.stream(),cardIdInRarityAndSeries).toList();
                activeRarityCardMap.put(rarity.getId(),mergedList);
            }

        }
    }

    private void loadRarityCardNames(){
        this.rarityCardMap = new HashMap<>();
        for(Rarity rarity: plugin.getRarityManager().getRarities()) {
            rarityCardMap.put(rarity.getId(),plugin.getStorage().getCardsInRarity(rarity.getId()).stream().map(TradingCard::getCardId).toList());
        }
    }

    @Override
    @Nullable
    public List<TradingCard> getRarityCardList(final String rarityId) {
        return plugin.getStorage().getCardsInRarity(rarityId);
    }

    @Override
    public List<String> getRarityCardListIds(final String rarityId) {
        return this.rarityCardMap.get(rarityId);
    }

    @Override
    public List<String> getActiveRarityCardIds(final String rarity) {
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
    public TradingCard getCard(final String cardId, final String rarityId, final boolean forcedShiny) {
        if (cards.containsKey(cardKey(rarityId, cardId))) {
            TradingCard card = cards.get(cardKey(rarityId, cardId)).get();
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
    public TradingCard getActiveCard(final String cardId, final String rarityId, final boolean forcedShiny) {
        final String cardKey = cardKey(rarityId, cardId);
        if (activeCards.contains(cardKey)) {
            return getCard(cardId, rarityId,forcedShiny);
        }
        //fallthrough
        //if it doesn't contain this card for some reason
        return NULL_CARD;
    }

    @Override
    public TradingCard getActiveCard(final String cardId, final String rarityId) {
        final String cardKey = cardKey(rarityId, cardId);
        if (activeCards.contains(cardKey)) {
            return getCard(cardId, rarityId);
        }
        //fallthrough
        //if it doesn't contain this card for some reason
        return NULL_CARD;
    }

    @Override
    public TradingCard getRandomCard(final String rarityId) {
        plugin.debug(TradingCardManager.class,"getRandomCard(),rarity=" + rarityId);
        var cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarityId).size());
        String randomCardName = getRarityCardList(rarityId).get(cardIndex).getCardId();
        return getCard(randomCardName, rarityId);
    }

    @Override
    public TradingCard getRandomActiveCard(final String rarityId) {
        return getRandomActiveCard(rarityId,false);
    }

    @Override
    public TradingCard getRandomCard(final String rarityId, final boolean forcedShiny) {
        plugin.debug(TradingCardManager.class,"getRandomCard(),rarity=" + rarityId);
        var cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarityId).size());
        String randomCardName = getRarityCardList(rarityId).get(cardIndex).getCardId();
        return getCard(randomCardName, rarityId, forcedShiny);
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
    public TradingCard getRandomActiveCard(final String rarityId, final boolean forcedShiny) {
        if (activeCards.isEmpty()) {
            plugin.debug(TradingCardManager.class,"There are no cards in the active series. Not dropping anything.");
            return NULL_CARD;
        }

        List<String> cardNames = getActiveRarityCardIds(rarityId);
        var cardIndex = plugin.getRandom().nextInt(cardNames.size());
        String randomCardName = cardNames.get(cardIndex);
        return getActiveCard(randomCardName, rarityId, forcedShiny);
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

    //TODO Should return a rarity.
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
            return card.getSeries().getId().equals(seriesId);
        }
        return false;
    }
}
