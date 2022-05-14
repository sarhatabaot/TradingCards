package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author sarhatabaot
 */
public class ActiveCardManager extends TradingCardManager {
    public ActiveCardManager(final TradingCards plugin) {
        super(plugin);
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
    protected LoadingCache<String, List<TradingCard>> loadRarityCardCache() {
        return CacheBuilder.newBuilder()
               .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public List<TradingCard> load(final String key) throws Exception {
                               List<TradingCard> cardList = new ArrayList<>();
                               for(Series series: plugin.getStorage().getActiveSeries()) {
                                   if(plugin.getStorage().getCardsInRarityAndSeriesCount(key,series.getId()) > 0) {
                                       List<TradingCard> tempList = plugin.getStorage().getCardsInRarityAndSeries(key,series.getId());
                                       if(tempList != null) {
                                           cardList = Stream.concat(cardList.stream(), tempList.stream()).toList();
                                       }
                                   }
                               }
                               return cardList;
                           }
                       }
                );
    }

    @Override
    public void preLoadSeriesCache() {
        try {
            this.seriesCardCache.getAll(plugin.getStorage().getActiveSeries().stream().map(Series::getId).toList());
        } catch (ExecutionException e) {
            //ignored
        }

    }

    public TradingCard getActiveCard(final String rarityId, final String seriesId, final String cardId) {
        return cache.getUnchecked(new CompositeCardKey(rarityId, seriesId, cardId));
    }

    public List<TradingCard> getActiveCards() {
        return cache.asMap().values().stream().toList();
    }

    public TradingCard getRandomActiveCardByRarity(final String rarityId) {
        final List<TradingCard> rarityCardList = rarityCardCache.getUnchecked(rarityId);
        int cardIndex = plugin.getRandom().nextInt(rarityCardList.size());
        return rarityCardList.get(cardIndex);
    }

    public TradingCard getRandomActiveCardBySeries(final String seriesId) {
        final List<TradingCard> seriesCardList = seriesCardCache.getUnchecked(seriesId);
        int cardIndex = plugin.getRandom().nextInt(seriesCardList.size());
        return seriesCardList.get(cardIndex);
    }

    @Override
    public List<CompositeCardKey> getKeys() {
        final List<CompositeCardKey> keys = new ArrayList<>();
        for(TradingCard card: plugin.getStorage().getActiveCards()) {
            keys.add(new CompositeCardKey(card.getRarity().getId(),card.getSeries().getId(),card.getCardId()));
        }
        return keys;

    }
}
