package net.tinetwork.tradingcards.api.economy;

/**
 * @author sarhatabaot
 */
public interface ResponseWrapper {
    boolean success();
    void setState(boolean state);
}
