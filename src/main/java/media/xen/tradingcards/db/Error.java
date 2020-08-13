package media.xen.tradingcards.db;

import media.xen.tradingcards.TradingCards;

import java.util.logging.Level;

public class Error {
	public static void execute(TradingCards plugin, Exception ex) {
		plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
	}

	public static void close(TradingCards plugin, Exception ex) {
		plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
	}
}