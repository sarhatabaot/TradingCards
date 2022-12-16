package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;

import com.github.sarhatabaot.kraken.core.logging.LoggerUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.CardManager;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalLog;
import org.apache.commons.rng.sampling.CollectionSampler;
import org.apache.commons.rng.sampling.DiscreteProbabilityCollectionSampler;
import org.apache.commons.rng.simple.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class AllCardManager extends TradingCardManager implements CardManager<TradingCard> {
    public static final EmptyCard NULL_CARD = new EmptyCard();
    private List<CompositeCardKey> keys;

    private final ActiveCardManager activeCardManager;

    public AllCardManager(final TradingCards plugin) {
        super(plugin);
        this.activeCardManager = new ActiveCardManager(plugin);

        initValues();
        this.plugin.getLogger().info(() -> InternalLog.CardManager.LOAD);
    }

    @Override
    protected LoadingCache<String, List<TradingCard>> loadRarityCardCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public @NotNull List<TradingCard> load(final @NotNull String key) throws Exception {
                               return plugin.getStorage().getCardsInRarity(key);
                               // Pretty large get, perhaps we should get a single card and then check where to put it..
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


    @Override
    public List<String> getCardsIdsInRarityAndSeries(final String rarityId, final String seriesId) {
        try {
            if (this.rarityAndSeriesCardCache.size() == 0) {
                return Collections.emptyList();
            }
            return this.rarityAndSeriesCardCache.get(CompositeRaritySeriesKey.of(rarityId, seriesId)).stream().map(TradingCard::getCardId).toList();
        } catch (ExecutionException e) {
            LoggerUtil.logSevereException(e);
            return Collections.emptyList();
        }
    }

    public void initValues() {
        plugin.getLogger().info(() -> InternalLog.CardManager.LOAD_CARDS.formatted(cache.asMap().keySet().size()));
        //plugin.debug(AllCardManager.class,StringUtils.join(cache.asMap().keySet(), ","));
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
        if (this.rarityCardCache.getIfPresent(rarityId) == null)
            return Collections.emptyList();

        return this.rarityCardCache.getUnchecked(rarityId).stream().map(TradingCard::getCardId).toList();
    }

    @Override
    public List<String> getActiveRarityCardIds(final String rarity) {
        try {
            List<String> allCardIds = new ArrayList<>();
            for (List<TradingCard> cardList : rarityCardCache.getAll(getActiveRarityIds()).values()) {
                allCardIds = Stream.concat(allCardIds.stream(), cardList.stream().map(TradingCard::getCardId)).toList();
            }
            return allCardIds;
        } catch (ExecutionException e) {
            plugin.debug(getClass(), e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Set<String> getActiveRarityIds() {
        return activeCardManager.rarityCardCache.asMap().keySet();
    }

    @Override
    public List<String> getActiveCards() {
        return activeCardManager.getActiveCards().stream().map(TradingCard::getCardId).toList();
    }

    @Override
    public TradingCard getCard(final String cardId, final String rarityId, final String seriesId) {
        try {
            return cache.get(new CompositeCardKey(rarityId, seriesId, cardId));
        } catch (ExecutionException | NullPointerException e) {
            LoggerUtil.logSevereException(e);
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
        plugin.debug(AllCardManager.class, InternalDebug.CardsManager.RANDOM_CARD.formatted(rarityId));
        int cardIndex = plugin.getRandom().nextInt(getRarityCardList(rarityId).size());
        return getRarityCardList(rarityId).get(cardIndex);
    }

    @Override
    public TradingCard getRandomCardBySeries(final String seriesId) {
        final List<TradingCard> seriesCardList = getSeriesCardList(seriesId);
        int cardIndex = plugin.getRandom().nextInt(seriesCardList.size());
        return seriesCardList.get(cardIndex);
    }

    public TradingCard getRandomCardByRarityAndSeries(final String rarityId, final String seriesId) {
        List<TradingCard> raritySeries = plugin.getStorage().getCardsInRarityAndSeries(rarityId, seriesId);
        if (raritySeries == null || raritySeries.isEmpty())
            return NULL_CARD;

        int cardIndex = plugin.getRandom().nextInt(raritySeries.size());
        return raritySeries.get(cardIndex);
    }

    @Override
    public TradingCard getRandomActiveCardByRarity(final String rarityId) {
        return activeCardManager.getRandomActiveCardByRarity(rarityId);
    }



    @Override
    public String getRandomRarityId(final DropType dropType) {
        Map<String,Double> rarityWeights = getRarityWeightMap(dropType);
        if(new HashSet<>(rarityWeights.values()).size() == 1) {
            //when everything is equal chance...
            CollectionSampler<String> sampler = new CollectionSampler<>(RandomSource.MWC_256.create(), rarityWeights.keySet());
            return sampler.sample();
        }

        DiscreteProbabilityCollectionSampler<String> sampler = new DiscreteProbabilityCollectionSampler<>(RandomSource.MWC_256.create(), rarityWeights);
        return sampler.sample();
    }

    private Map<String,Double> getRarityWeightMap(DropType dropType) {
        Map<String,Double> rarityWeight = new HashMap<>(); //id, weight
        for (String rarity : plugin.getRarityManager().getRarityIds()) {
            rarityWeight.put(rarity, (double) plugin.getChancesConfig().getChance(rarity).getFromMobType(dropType));
        }
        return rarityWeight;
    }


    @Override
    public boolean containsCard(final String cardId, final String rarityId, final String seriesId) {
        CompositeCardKey cardKey = new CompositeCardKey(rarityId, seriesId, cardId);
        return cache.getIfPresent(cardKey) != null;
    }

    @Override
    public List<CompositeCardKey> getKeys() {
        if (this.keys == null) {
            this.keys = new ArrayList<>();
            for (TradingCard card : plugin.getStorage().getCards()) {
                keys.add(new CompositeCardKey(card.getRarity().getId(), card.getSeries().getId(), card.getCardId()));
            }
        }

        return keys;
    }

    @Override
    public List<String> getCards() {
        return getKeys().stream().map(CompositeCardKey::toString).toList();
    }
}
