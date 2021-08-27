package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChatUtil {
	private static final char ALT_COLOR_CHAR = '&';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + ALT_COLOR_CHAR + "[0-9A-FK-ORX]");
	private static TradingCards plugin;

	public static void init(final TradingCards plugin){
		ChatUtil.plugin = plugin;
	}

	public static void sendPrefixedMessage(final CommandSender toWhom, final String message) {
		sendMessage(toWhom, plugin.getPrefixedMessage(message));
	}

	public static void sendMessage(final CommandSender toWhom, final String message) {
		toWhom.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static String color(Component component) {
		return LegacyComponentSerializer.builder().character('&').build().serialize(component);
	}

	public static String color(String text) {
		return ChatColor.translateAlternateColorCodes('&',text);
	}

	/**
	 * Strips the given message of all color codes
	 *
	 * @param input String to strip of color
	 * @return A copy of the input string, without any coloring
	 */
	@Contract("!null -> !null; null -> null")
	@Nullable
	public static String stripAllColor(@Nullable final String input) {
		if (input == null) {
			return null;
		}

		return ChatColor.stripColor(STRIP_COLOR_PATTERN.matcher(input).replaceAll(""));
	}

	public static List<String> wrapString(@NotNull String s) {
		String parsedString = ChatColor.stripColor(s);
		String addedString = WordUtils.wrap(parsedString, plugin.getGeneralConfig().infoLineLength(), "\n", true);
		String[] splitString = addedString.split("\n");
		List<String> finalArray = new ArrayList<>();

		for (String ss : splitString) {
			plugin.debug(ChatColor.getLastColors(ss));
			finalArray.add(ChatUtil.color("&f &7- &f" + ss));
		}

		return finalArray;
	}
	public static void sendMessage(final CommandSender target, final Component text) {
		target.sendMessage(color(text));
	}
}
