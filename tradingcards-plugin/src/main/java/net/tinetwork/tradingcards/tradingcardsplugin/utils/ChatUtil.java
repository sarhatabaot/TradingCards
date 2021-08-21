package net.tinetwork.tradingcards.tradingcardsplugin.utils;

import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {
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

	public static void sendMessage(final CommandSender target, final Component text) {
		target.sendMessage(color(text));
	}
}
