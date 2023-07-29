package net.tinetwork.tradingcards.api.addons;


import java.util.Map;

/**
 * @author sarhatabaot
 */
public interface AddonManager {
    /**
     * Reload all addons
     */
    void reloadAddons();

    /**
     * Reload a specific addon
     * @param id addon id
     */
    void reloadAddon(final String id);

    Map<String, TradingCardsAddon> getAddons();
}
