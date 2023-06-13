package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author sarhatabaot
 */
public abstract class TradingCardManager extends Manager<CompositeCardKey, TradingCard> {
    protected final LoadingCache<String, List<TradingCard>> rarityCardCache;
    protected final LoadingCache<String, List<TradingCard>> seriesCardCache;
    protected final LoadingCache<CompositeRaritySeriesKey, List<TradingCard>> rarityAndSeriesCardCache;

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
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> {
                            plugin.debug(TradingCardManager.class, "Loaded Card=%s into cache.".formatted(key.toString()));
                            return plugin.getStorage().getCard(key.cardId(), key.rarityId(), key.seriesId()).get();
                        }
                );
    }


    @Contract(" -> new")
    private @NotNull LoadingCache<String, List<TradingCard>> loadSeriesCardCache() {
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> plugin.getStorage().getCardsInSeries(key));
    }

    private @NotNull LoadingCache<CompositeRaritySeriesKey, List<TradingCard>> loadRarityAndSeriesCardCache() {
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> {
                            final List<TradingCard> cardList = plugin.getStorage().getCardsInRarityAndSeries(key.rarityId(), key.seriesId());
                            return Objects.requireNonNullElse(cardList, Collections.emptyList());
                        }
                );
    }

    private void preLoadRarityAndSeriesCache() {
        this.rarityAndSeriesCardCache.getAll(loadRaritySeriesKeys());

    }

    private @NotNull List<CompositeRaritySeriesKey> loadRaritySeriesKeys() {
        List<CompositeRaritySeriesKey> keyList = new ArrayList<>();
        for (String seriesId : seriesCardCache.asMap().keySet()) {
            for (String rarityId : rarityCardCache.asMap().keySet()) {
                keyList.add(new CompositeRaritySeriesKey(rarityId, seriesId));
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
