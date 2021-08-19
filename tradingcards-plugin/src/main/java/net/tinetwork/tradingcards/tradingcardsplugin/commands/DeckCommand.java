package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@CommandAlias("deck|decks")
public class DeckCommand extends BaseCommand {
	private final TradingCards plugin;

	public DeckCommand(final TradingCards plugin) {
		this.plugin = plugin;
	}


	@Default
	@CommandPermission("cards.decks.open")
	@Description("Get a deck item. Or opens a deck.")
	public void onGetDeck(final Player player, final int deckNumber) {
		Validate.notNull(player, "Cannot run this command from console, or there was a problem getting the player object.");

		if (!player.hasPermission("cards.decks." + deckNumber)) {
			ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesOldConfig().maxDecks));
			return;
		}

		if (plugin.getMainConfig().useDeckItems) {
			if (!plugin.getDeckManager().hasDeck(player, deckNumber)) {
				CardUtil.dropItem(player, plugin.getDeckManager().createDeck(player, deckNumber));
			} else {
				ChatUtil.sendPrefixedMessage(player, plugin.getMessagesOldConfig().alreadyHaveDeck);
			}
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			if (plugin.getMainConfig().decksInCreative) {
				plugin.getDeckManager().openDeck(player, deckNumber);
				return;
			}
			ChatUtil.sendPrefixedMessage(player, plugin.getMessagesOldConfig().deckCreativeError);
			return;
		}
		plugin.getDeckManager().openDeck(player, deckNumber);

	}
}
