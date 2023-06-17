package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import net.tinetwork.tradingcards.api.config.ColorSeries;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sarhatabaot
 */
public class LoggerUtil {
    private static Logger logger;
    public static final List<String> COLORS = List.of("info=", "about=", "type=", "series=", "rarity=");
    public static final ColorSeries DEFAULT_COLORS = new ColorSeries("&a", "&b", "&e", "&c", "&6");

    private LoggerUtil() {
        throw new UnsupportedOperationException();
    }

    public static void init(final Logger logger) {
        LoggerUtil.logger = logger;
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

    public static void logAndMessage(final CommandSender sender, final String message) {
        if(!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatUtil.color(message));
        }
        logger.log(Level.INFO, () -> ChatColor.stripColor(message));
    }
}
