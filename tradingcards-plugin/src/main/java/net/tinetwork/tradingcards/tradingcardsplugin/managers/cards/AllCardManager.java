package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.chance.Chance;
import net.tinetwork.tradingcards.api.model.chance.EmptyChance;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil.cardKey;

public class AllCardManager extends TradingCardManager implements CardManager<TradingCard>{
    public static final EmptyCard NULL_CARD = new EmptyCard();

    private final ActiveCardManager activeCardManager;

    //This stores the ids of cards from a single rarity, over multiple files
    //rarity, list-card-id
    private Map<String, List<String>> rarityCardMap;


    private Map<String, Map<String, List<String>>> cardsInRarityAndSeriesIds;

    public AllCardManager(final TradingCards plugin) {
        super(plugin);
        this.activeCardManager = new ActiveCardManager(plugin);

        initValues();
        this.plugin.getLogger().info(() -> InternalLog.CardManager.LOAD);
    }

    @Override
    public LoadingCache<CompositeCardKey, TradingCard> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(3000)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public TradingCard load(final CompositeCardKey key) throws Exception {
                               return plugin.getStorage().getCard(key.cardId(), key.rarityId(), key.seriesId()).get();
                           }
                       }
                );
    }

    @Override
    protected LoadingCache<String, List<TradingCard>> loadRarityCardCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(3000)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public List<TradingCard> load(final String key) throws Exception {
                               return plugin.getStorage().getCardsInRarity(key);
                           }
                       }
                );
    }

    @Override
    public void preLoadRarityCache() {
        List<String> rarities = plugin.getRarityManager().getRarities().stream().map(Rarity::getId).toList();
        try {
            this.rarityCardCache.getAll(rarities);
        } catch (ExecutionException e) {
            //ignored
        }
    }

    @Override
    public void preLoadSeriesCache() {
        try {
            this.seriesCardCache.getAll(plugin.getStorage().getActiveSeries().stream().map(Series::getId).toList());
        } catch (ExecutionException e) {
            //ignored
        }
    }



    private @NotNull List<CompositeCardKey> loadCompositeKeys() {
        final List<CompositeCardKey> keys = new ArrayList<>();
        for(TradingCard card: plugin.getStorage().getCards()) {
            keys.add(new CompositeCardKey(card.getRarity().getId(),card.getSeries().getId(),card.getCardId()));
        }
        return keys;
    }
    @Override
    public List<String> getCardsIdsInRarityAndSeries(final String rarityId, final String seriesId) {
        return this.cardsInRarityAndSeriesIds.get(rarityId).get(seriesId);
    }

    public void initValues() {
        loadAllCards();
        loadCardsInRaritiesAndSeriesIds();
        plugin.getLogger().info(() -> InternalLog.CardManager.LOAD_CARDS.formatted(cache.asMap().keySet().size()));
        plugin.getLogger().info(() -> InternalLog.CardManager.LOAD_RARITIES.formatted(rarityCardMap.keySet().size()));
        plugin.debug(AllCardManager.class,StringUtils.join(rarityCardMap.keySet(), ","));
        plugin.debug(AllCardManager.class,StringUtils.join(cache.asMap().keySet(), ","));
    }

    private void loadCardsInRaritiesAndSeriesIds() {
        cardsInRarityAndSeriesIds = new HashMap<>();
        plugin.debug(AllCardManager.class, InternalDebug.CardsManager.LOAD_RARITY_CARDS_IDS);
        for(String rarityId: plugin.getRarityManager().getRarityIds()) {
            cardsInRarityAndSeriesIds.putIfAbsent(rarityId, new HashMap<>());
            plugin.debug(AllCardManager.class,InternalDebug.CardsManager.RARITY_ID.formatted(rarityId));
            for(String seriesId: plugin.getSeriesManager().getSeriesIds()) {
                plugin.debug(AllCardManager.class,InternalDebug.CardsManager.SERIES_ID.formatted(seriesId));
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
        loadRarityCardNames();
    }

    private void loadRarityCardNames(){
        this.rarityCardMap = new HashMap<>();
        for(Rarity rarity: plugin.getRarityManager().getRarities()) {
            rarityCardMap.put(rarity.getId(),
                    plugin.getStorage().getCardsInRarity(rarity.getId()).stream().map(TradingCard::getCardId).toList());
        }
    }

    @Override
    public List<TradingCard> getRarityCardList(final String rarityId) {
        return plugin.getStorage().getCardsInRarity(rarityId);
    }

    public List<TradingCard> getSeriesCardList(final String seriesId) {
        return plugin.getStorage().getCardsInSeries(seriesId);
    }

    @Override
    public List<String> getRarityCardListIds(final String rarityId) {
        return this.rarityCardMap.get(rarityId);
    }

    @Override
    public List<String> getActiveRarityCardIds(final String rarity) {
        try {
            List<String> allCardIds = new ArrayList<>();
            for(List<TradingCard> cardList: rarityCardCache.getAll(getActiveRarityIds()).values()) {
                allCardIds = Stream.concat(allCardIds.stream(), cardList.stream().map(TradingCard::getCardId)).toList();
            }
            return allCardIds;
        } catch (ExecutionException e) {
            plugin.debug(getClass(),e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> getRarityNames() {
        return rarityCardMap.keySet();
    }

    @Override
    public Set<String> getActiveRarityIds() {
        return rarityCardCache.asMap().keySet();
    }

    @Override
    public List<String> getActiveCards() {
        return activeCardManager.getActiveCards().stream().map(TradingCard::getCardId).toList();
    }

    @Override
    public TradingCard getCard(final String cardId, final String rarityId, final String seriesId) {
        try {
            return cache.getUnchecked(new CompositeCardKey(rarityId,seriesId,cardId));
        } catch (NullPointerException e) {
            return NULL_CARD;
        }
    }


    @Override
    public TradingCard getActiveCard(final String cardId, final String rarityId, final String seriesId) {
        try {
            return activeCardManager.getActiveCard(rarityId, seriesId, cardId);
        } catch (NullPointerException e) {
            return NULL_CARD;
        }
    }

    @Override
    public TradingCard getRandomCardByRarity(final String rarityId) {
        plugin.debug(AllCardManager.class,InternalDebug.CardsManager.RANDOM_CARD.formatted(rarityId));
        var cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarityId).size());
        return getRarityCardList(rarityId).get(cardIndex);
    }

    @Override
    public TradingCard getRandomCardBySeries(final String seriesId) {
        final List<TradingCard> seriesCardList = getSeriesCardList(seriesId);
        int cardIndex = plugin.getRandom().nextInt(seriesCardList.size());
        return seriesCardList.get(cardIndex);
    }

    public TradingCard getRandomCardByRarityAndSeries(final String rarityId, final String seriesId) {
        List<TradingCard> raritySeries = plugin.getStorage().getCardsInRarityAndSeries(rarityId,seriesId);
        int cardIndex = plugin.getRandom().nextInt(raritySeries.size());
        return raritySeries.get(cardIndex);
    }

    @Override
    public TradingCard getRandomActiveCardByRarity(final String rarityId) {
        return activeCardManager.getRandomActiveCardByRarity(rarityId);
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

    @Override
    public String getRandomRarityId(DropType dropType, boolean alwaysDrop) {
        int randomDropChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        int mobDropChance = getGeneralMobChance(dropType);
        plugin.debug(AllCardManager.class,InternalDebug.CardsManager.DROP_CHANCE.formatted(randomDropChance,alwaysDrop,dropType,mobDropChance));
        if (!alwaysDrop && randomDropChance > mobDropChance) {
            return "None";
        }

        int randomRarityChance = plugin.getRandom().nextInt(CardUtil.RANDOM_MAX) + 1;
        plugin.debug(AllCardManager.class,InternalDebug.CardsManager.RARITY_CHANCE.formatted(randomRarityChance));

        TreeSet<String> rarityKeys = new TreeSet<>(plugin.getRarityManager().getRarityIds());
        for (String rarity : rarityKeys.descendingSet()) { //todo This should be ordered out of storage, we shouldn't have to order it here.
            Chance chance = plugin.getChancesConfig().getChance(rarity);
            if (chance instanceof EmptyChance)
                return "None";

            int chanceInt = chance.getFromMobType(dropType);
            if (randomRarityChance < chanceInt)
                return rarity;
        }
        return "None";
    }


    @Override
    public boolean containsCard(final String cardId, final String rarityId, final String seriesId) {
        CompositeCardKey cardKey = new CompositeCardKey(rarityId,seriesId,cardId);
        return cache.getIfPresent(cardKey) != null;
    }

    @Override
    public List<CompositeCardKey> getKeys() {
        final List<CompositeCardKey> keys = new ArrayList<>();
        for(TradingCard card: plugin.getStorage().getCards()) {
            keys.add(new CompositeCardKey(card.getRarity().getId(),card.getSeries().getId(),card.getCardId()));
        }
        return keys;
    }
}
