package net.tinetwork.tradingcards.tradingcardsapi.blacklist;

public interface Blacklist<T> {
    boolean isAllowed(T value);

    void add(T value);


    void remove(T value);

    /**
     * Returns the mode the whitelist is in.
     * @return
     */
    WhitelistMode getMode();
}
