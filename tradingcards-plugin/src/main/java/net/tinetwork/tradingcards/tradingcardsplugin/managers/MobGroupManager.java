package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import com.google.common.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.Cacheable;
import net.tinetwork.tradingcards.api.model.MobGroup;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;

import java.util.List;
import java.util.concurrent.Future;

/**
 * @author sarhatabaot
 */
public class MobGroupManager extends Manager<String, MobGroup> implements Cacheable<String, MobGroup> {
    public MobGroupManager(final TradingCards plugin) {
        super(plugin);
    }

    public Future<MobGroup> getMobGroup(final String groupId) {
        return cache.get(groupId);
    }

    @Override
    public LoadingCache<String, MobGroup> loadCache() {
        return null;
    }

    @Override
    public List<String> getKeys() {
        return null;
    }
}
