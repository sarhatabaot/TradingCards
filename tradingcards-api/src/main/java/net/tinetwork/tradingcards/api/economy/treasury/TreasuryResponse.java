package net.tinetwork.tradingcards.api.economy.treasury;

import net.tinetwork.tradingcards.api.economy.ResponseWrapper;

/**
 * @author sarhatabaot
 */
public class TreasuryResponse implements ResponseWrapper {
    private boolean state;

    public TreasuryResponse(final boolean state) {
        this.state = state;
    }

    public TreasuryResponse() {
    }

    public void setState(final boolean state) {
        this.state = state;
    }

    @Override
    public boolean success() {
        return state;
    }
}
