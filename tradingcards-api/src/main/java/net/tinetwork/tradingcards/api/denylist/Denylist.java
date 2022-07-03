package net.tinetwork.tradingcards.api.denylist;

public interface Denylist<T> {
    /**
     * Return if the value is allowed.
     * @param value value
     * @return
     */
    boolean isAllowed(T value);

    /**
     * Add a value to the blacklist.
     * @param value value
     */
    void add(T value);

    /**
     * Remove a value from the black.
     * @param value T
     */
    void remove(T value);

    /**
     * Returns the mode the whitelist is in.
     * @return mode
     */
    AllowlistMode getMode();
}
