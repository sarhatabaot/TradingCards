package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sarhatabaot
 */
public abstract class TradingCardManager extends Manager<CompositeCardKey, TradingCard> {
    protected final LoadingCache<String, List<TradingCard>> rarityCardCache;
    protected final LoadingCache<String, List<TradingCard>> seriesCardCache;

    protected TradingCardManager(final TradingCards plugin) {
        super(plugin);
        this.rarityCardCache = loadRarityCardCache();
        this.seriesCardCache = loadSeriesCardCache();
        preLoadRarityCache();
        preLoadSeriesCache();
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

    protected abstract LoadingCache<String, List<TradingCard>> loadRarityCardCache();
//    @Contract(" -> new")
//    protected @NotNull LoadingCache<String, List<TradingCard>> loadRarityCardCache() {
//        return CacheBuilder.newBuilder()
//                .maximumSize(3000)
//                .refreshAfterWrite(5, TimeUnit.MINUTES)
//                .build(new CacheLoader<>() {
//                           @Override
//                           public List<TradingCard> load(final String key) throws Exception {
//                               return plugin.getStorage().getCardsInRarity(key);
//                           }
//                       }
//                );
//    }

    @Contract(" -> new")
    private @NotNull LoadingCache<String, List<TradingCard>> loadSeriesCardCache() {
        return CacheBuilder.newBuilder()
                .maximumSize(3000)
                .refreshAfterWrite(5, TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                           @Override
                           public List<TradingCard> load(final String key) throws Exception {
                               return plugin.getStorage().getCardsInSeries(key);
                           }
                       }
                );
    }

    public abstract void preLoadRarityCache();

    public abstract void preLoadSeriesCache();


}
