package net.tinetwork.tradingcards.tradingcardsplugin.messages;

public final class InternalExceptions {
    public static final String NO_ITEM_META = "Could not get ItemMeta for this item.";
    public static final String INVALID_CHANCE = "%s chance must be between 1 and 100,000. This chance was %s";
    public static final String UTIL_CLASS = "Util class. It cannot be instantiated.";
    public static final String NO_SCHEMA = "Couldn't locate schema file for %s";
    public static final String DATA_SOURCE_NULL = "Unable to get a connection from the pool. (datasource is null)";
    public static final String GET_CONNECTION_NULL = "Unable to get a connection from the pool. (getConnection returned null)";

}