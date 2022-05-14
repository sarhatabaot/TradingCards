package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author sarhatabaot
 */
public abstract class TradingCardManager extends Manager<CompositeCardKey, TradingCard> {
    protected final LoadingCache<String, List<TradingCard>> rarityCardCache;
    protected final LoadingCache<String, List<TradingCard>> seriesCardCache;
    protected final LoadingCache<CompositeRaritySeriesKey,List<TradingCard>> rarityAndSeriesCardCache;

    protected TradingCardManager(final TradingCards plugin) {
        super(plugin);
        this.rarityCardCache = loadRarityCardCache();
        this.seriesCardCache = loadSeriesCardCache();
        preLoadRarityCache();
        preLoadSeriesCache();

        this.rarityAndSeriesCardCache = loadRarityAndSeriesCardCache();
        preLoadRarityAndSeriesCache();
    }

    @Override
    public LoadingCache<CompositeCardKey, TradingCard> loadCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public TradingCard load(final CompositeCardKey key) throws Exception {
                               return plugin.getStorage().getCard(key.cardId(), key.rarityId(), key.seriesId()).get();
                           }
                       }
                );
    }



    @Contract(" -> new")
    private @NotNull LoadingCache<String, List<TradingCard>> loadSeriesCardCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public List<TradingCard> load(final String key) throws Exception {
                               return plugin.getStorage().getCardsInSeries(key);
                           }
                       }
                );
    }

    private @NotNull LoadingCache<CompositeRaritySeriesKey,List<TradingCard>> loadRarityAndSeriesCardCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(),TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                    @Override
                    public List<TradingCard> load(final CompositeRaritySeriesKey key) throws Exception {
                        List<TradingCard> cardList = plugin.getStorage().getCardsInRarityAndSeries(key.rarityId(),key.seriesId());
                        if(cardList == null)
                            return Collections.emptyList();

                        return cardList;
                    }
                });
    }

    private void preLoadRarityAndSeriesCache() {
        try {
            this.rarityAndSeriesCardCache.getAll(loadRaritySeriesKeys());
        } catch (ExecutionException e) {
            //ignored
        }
    }

    private @NotNull List<CompositeRaritySeriesKey> loadRaritySeriesKeys() {
        List<CompositeRaritySeriesKey> keyList = new ArrayList<>();
        for (String seriesId: seriesCardCache.asMap().keySet()) {
            for(String rarityId: rarityCardCache.asMap().keySet()) {
                keyList.add(new CompositeRaritySeriesKey(rarityId,seriesId));
            }
        }
        return keyList;
    }

    protected abstract LoadingCache<String, List<TradingCard>> loadRarityCardCache();

    public abstract void preLoadRarityCache();

    public abstract void preLoadSeriesCache();

    public LoadingCache<String, List<TradingCard>> getRarityCardCache() {
        return rarityCardCache;
    }

    public LoadingCache<String, List<TradingCard>> getSeriesCardCache() {
        return seriesCardCache;
    }

    public LoadingCache<CompositeRaritySeriesKey, List<TradingCard>> getRarityAndSeriesCardCache() {
        return rarityAndSeriesCardCache;
    }
}
