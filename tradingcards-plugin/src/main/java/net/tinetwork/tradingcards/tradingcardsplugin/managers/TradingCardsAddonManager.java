package net.tinetwork.tradingcards.tradingcardsplugin.managers;

import net.tinetwork.tradingcards.api.addons.TradingCardsAddon;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;

import java.util.HashMap;
import java.util.Map;

/**
 * @author sarhatabaot
 */
public class TradingCardsAddonManager implements net.tinetwork.tradingcards.api.manager.AddonManager {
    private final TradingCards plugin;
    private final Map<String, TradingCardsAddon> addons;

    public TradingCardsAddonManager(final TradingCards plugin) {
        this.plugin = plugin;
        this.addons = new HashMap<>();
    }

    @Override
    public void reloadAddons() {
        for(TradingCardsAddon addon: addons.values()){
            addon.onReload();
        }
        plugin.getLogger().info("Reloaded "+addons.size()+" addons.");
    }

    @Override
    public void reloadAddon(final String id) {
        this.addons.get(id).onReload();
    }

    @Override
    public Map<String, TradingCardsAddon> getAddons() {
        return this.addons;
    }
}
