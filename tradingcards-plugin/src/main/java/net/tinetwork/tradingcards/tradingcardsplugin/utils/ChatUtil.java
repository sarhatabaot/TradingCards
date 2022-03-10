package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ChatUtil {
	private static TradingCards plugin;
	private ChatUtil() {
		throw new UnsupportedOperationException();
	}
	public static void init(final TradingCards plugin){
		ChatUtil.plugin = plugin;
	}

	public static void sendPrefixedMessage(final CommandSender toWhom, final String message) {
		sendMessage(toWhom, plugin.getPrefixedMessage(message));
	}

	public static void sendMessage(final @NotNull CommandSender toWhom, final String message) {
		toWhom.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}

	public static @NotNull String color(Component component) {
		return LegacyComponentSerializer.builder().character('&').build().serialize(component);
	}

	@Contract("_ -> new")
	public static @NotNull String color(String text) {
		return ChatColor.translateAlternateColorCodes('&',text);
	}

	public static @NotNull List<String> wrapString(@NotNull String s) {
		String parsedString = ChatColor.stripColor(s);
		String addedString = WordUtils.wrap(parsedString, plugin.getGeneralConfig().infoLineLength(), "\n", true);
		String[] splitString = addedString.split("\n");
		List<String> finalArray = new ArrayList<>();

		for (String ss : splitString) {
			finalArray.add(ChatUtil.color("&f &7- &f" + ss));
		}

		return finalArray;
	}
	public static void sendMessage(final @NotNull CommandSender target, final Component text) {
		target.sendMessage(color(text));
	}
}
