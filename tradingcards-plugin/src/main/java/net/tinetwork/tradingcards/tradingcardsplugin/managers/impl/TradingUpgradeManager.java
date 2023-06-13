package net.tinetwork.tradingcards.tradingcardsplugin.managers.impl;

import com.github.benmanes.caffeine.cache.Caffeine;

import com.github.benmanes.caffeine.cache.LoadingCache;
import net.tinetwork.tradingcards.api.manager.UpgradeManager;
import net.tinetwork.tradingcards.api.model.Upgrade;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.Manager;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author sarhatabaot
 */
public class TradingUpgradeManager extends Manager<String, Upgrade> implements UpgradeManager {
    public TradingUpgradeManager(final TradingCards plugin) {
        super(plugin);
    }

    @Override
    public LoadingCache<String, Upgrade> loadCache() {
        return Caffeine.newBuilder()
                .maximumSize(plugin.getAdvancedConfig().getUpgrades().maxCacheSize())
                .refreshAfterWrite(plugin.getAdvancedConfig().getUpgrades().refreshAfterWrite(), TimeUnit.MINUTES)
                .build(key -> {
                    plugin.debug(TradingRarityManager.class, InternalDebug.LOADED_INTO_CACHE.formatted(key));
                    return plugin.getStorage().getUpgrade(key);
                });
    }

    @Override
    public List<String> getKeys() {
        return getUpgrades().stream().map(Upgrade::id).toList();
    }

    public List<Upgrade> getUpgrades() {
        return plugin.getStorage().getUpgrades();
    }

    public Upgrade getUpgrade(final String upgradeId) {
        return this.cache.get(upgradeId);
    }

    public boolean containsUpgrade(final String upgradeId) {
        for (Upgrade upgrade : getUpgrades()) {
            if (upgrade.id().equals(upgradeId)) {
                return true;
            }
        }
        return false;
    }
}
