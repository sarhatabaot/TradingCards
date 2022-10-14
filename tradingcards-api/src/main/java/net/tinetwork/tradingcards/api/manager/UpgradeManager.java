package net.tinetwork.tradingcards.api.manager;

import net.tinetwork.tradingcards.api.model.Upgrade;

import java.util.List;

/**
 * @author sarhatabaot
 */
public interface UpgradeManager {
    List<Upgrade> getUpgrades();
    Upgrade getUpgrade(final String upgradeId);
    boolean containsUpgrade(final String upgradeId);
}
