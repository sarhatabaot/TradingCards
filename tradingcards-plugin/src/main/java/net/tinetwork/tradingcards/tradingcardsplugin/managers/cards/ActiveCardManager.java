package net.tinetwork.tradingcards.tradingcardsplugin.managers.cards;


import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;

import java.util.ArrayList;
import java.util.List;
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
        this.rarityCardCache.getAll(rarities);
    }

    @Override
    protected LoadingCache<String, List<TradingCard>> loadRarityCardCache() {
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getCards().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getCards().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> {
                            List<TradingCard> cardList = new ArrayList<>();
                            for (Series series : plugin.getStorage().getActiveSeries()) {
                                if (plugin.getStorage().getCardsInRarityAndSeriesCount(key, series.getId()) > 0) {
                                    List<TradingCard> tempList = plugin.getStorage().getCardsInRarityAndSeries(key, series.getId());
                                    if (tempList != null) {
                                        cardList = Stream.concat(cardList.stream(), tempList.stream()).toList();
                                    }
                                }
                            }
                            return cardList;
                        }
                );
    }

    @Override
    public void preLoadSeriesCache() {
        this.seriesCardCache.getAll(plugin.getStorage().getActiveSeries().stream().map(Series::getId).toList());
    }

    public TradingCard getActiveCard(final String rarityId, final String seriesId, final String cardId) {
        return cache.get(new CompositeCardKey(rarityId, seriesId, cardId));
    }

    public List<TradingCard> getActiveCards() {
        return cache.asMap().values().stream().toList();
    }

    public TradingCard getRandomActiveCardByRarity(final String rarityId) {
        final List<TradingCard> rarityCardList = rarityCardCache.get(rarityId);
        int cardIndex = plugin.getRandom().nextInt(rarityCardList.size());
        return rarityCardList.get(cardIndex);
    }

    public TradingCard getRandomActiveCardBySeries(final String seriesId) {
        final List<TradingCard> seriesCardList = seriesCardCache.get(seriesId);
        int cardIndex = plugin.getRandom().nextInt(seriesCardList.size());
        return seriesCardList.get(cardIndex);
    }

    @Override
    public List<CompositeCardKey> getKeys() {
        final List<CompositeCardKey> keys = new ArrayList<>();
        for (TradingCard card : plugin.getStorage().getActiveCards()) {
            keys.add(new CompositeCardKey(card.getRarity().getId(), card.getSeries().getId(), card.getCardId()));
        }
        return keys;

    }
}
