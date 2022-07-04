package net.tinetwork.tradingcards.api.economy.vault;

import net.milkbowl.vault.economy.EconomyResponse;
import net.tinetwork.tradingcards.api.economy.ResponseWrapper;

/**
 * @author sarhatabaot
 */
public class VaultResponse implements ResponseWrapper {
    private final EconomyResponse economyResponse;

    public VaultResponse(final EconomyResponse economyResponse) {
        this.economyResponse = economyResponse;
    }

    @Override
    public boolean success() {
        return economyResponse.transactionSuccess();
    }

    @Override
    public void setState(final boolean state) {
        // nothing to do here...
    }
}
