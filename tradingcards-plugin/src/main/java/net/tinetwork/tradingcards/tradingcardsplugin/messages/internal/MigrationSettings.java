package net.tinetwork.tradingcards.tradingcardsplugin.messages.internal;
public final class MigrationSettings {

	public static final String LATEST_DB_VERSION = "8";
	public static final String LATEST_BASE_DB_VERSION = "8";

	private MigrationSettings() {
		throw new UnsupportedOperationException(InternalExceptions.UTIL_CLASS);
	}
}