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
import java.util.Arrays;
import java.util.List;

public class ChatUtil {
    private static final char ALT_COLOR_CHAR = '&';
    private static TradingCards plugin;

    private ChatUtil() {
        throw new UnsupportedOperationException();
    }

    public static void init(final TradingCards plugin) {
        ChatUtil.plugin = plugin;
    }

    public static void sendPrefixedMessage(final CommandSender target, final String message) {
        sendMessage(target, plugin.getPrefixedMessage(message));
    }

    public static void sendMessage(final @NotNull CommandSender target, final String message) {
        com.github.sarhatabaot.kraken.core.chat.ChatUtil.sendMessage(target, message);
    }

    public static void sendPrefixedMessages(final CommandSender target, final String... messages) {
        List<String> prefixedMessages = new ArrayList<>();
        Arrays.stream(messages).forEach(message -> prefixedMessages.add(plugin.getPrefixedMessage(message)));
        com.github.sarhatabaot.kraken.core.chat.ChatUtil.sendMessage(target, prefixedMessages.toArray(new String[0]));
    }

    //We assume that the length of messages & args are the same, we should figure out a way to add a warning about this in intellij
    public static void sendPrefixedMessages(final CommandSender target, final String @NotNull [] messages, Object... args) {
        for (int i = 0; i < messages.length; i++) {
            sendPrefixedMessage(target, messages[i].formatted(args[i]));
        }
    }

    public static @NotNull String color(Component component) {
        return LegacyComponentSerializer.builder().character(ALT_COLOR_CHAR).build().serialize(component);
    }

    @Contract("_ -> new")
    public static @NotNull String color(String text) {
        return com.github.sarhatabaot.kraken.core.chat.ChatUtil.color(text);
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
