package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@CommandAlias("deck")
public class DeckCommand extends BaseCommand {
	private final TradingCards plugin;
	private final TradingDeckManager deckManager;

	public DeckCommand(final @NotNull TradingCards plugin) {
		this.plugin = plugin;
		this.deckManager = plugin.getDeckManager();
	}

	@Default
	@CommandPermission(Permissions.User.Use.DECK)
	@Description("Get a deck item. Or opens a deck.")
	public void onGetDeck(final Player player,@Default("1") int deckNumber) {
		Validate.notNull(player, InternalMessages.DeckCommand.CANNOT_RUN_FROM_CONSOLE);

		if (!player.hasPermission("cards.decks." + deckNumber)) {
			ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().maxDecks()));
			return;
		}

		if (plugin.getGeneralConfig().useDeckItem()) {
			dropDeckItem(player,deckNumber);
			return;
		}

		if(!plugin.getGeneralConfig().deckInCreative() && player.getGameMode() == GameMode.CREATIVE) {
			ChatUtil.sendMessage(player, plugin.getMessagesConfig().deckCreativeError());
			return;
		}

		deckManager.openDeck(player, deckNumber);
	}

	private void dropDeckItem(final Player player, int deckNumber){
		if (!deckManager.hasDeckItem(player, deckNumber)) {
			CardUtil.dropItem(player, deckManager.getDeckItem(player, deckNumber));
		} else {
			ChatUtil.sendMessage(player, plugin.getMessagesConfig().alreadyHaveDeck());
		}
	}
}
