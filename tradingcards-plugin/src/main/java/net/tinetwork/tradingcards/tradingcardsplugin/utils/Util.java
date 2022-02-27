package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sarhatabaot
 */
public class Util {
    private static Logger logger;
    public static final List<String> COLORS = List.of("info=", "about=", "type=", "series=", "rarity=");

    private Util() {
        throw new UnsupportedOperationException();
    }

    public static void init(final Logger logger) {
        Util.logger = logger;
    }
    public static void logSevereException(Exception e) {
        logException(Level.SEVERE,e);
    }

    public static void logWarningException(Exception e) {
        logException(Level.WARNING,e);
    }

    public static void logException(Level level, Exception e) {
        logger.log(level,e.getMessage(),e);
    }
}
