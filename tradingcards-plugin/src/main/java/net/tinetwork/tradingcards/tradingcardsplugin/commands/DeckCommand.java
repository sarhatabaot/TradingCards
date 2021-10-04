package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("deck")
public class DeckCommand extends BaseCommand {
	private final TradingCards plugin;

	public DeckCommand(final TradingCards plugin) {
		this.plugin = plugin;
	}


	@Default
	@CommandPermission(Permissions.USE_DECK)
	@Description("Get a deck item. Or opens a deck.")
	public void onGetDeck(final Player player, int deckNumber) {
		Validate.notNull(player, "Cannot run this command from console, or there was a problem getting the player object.");

		if (!player.hasPermission("cards.decks." + deckNumber)) {
			ChatUtil.sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().maxDecks()));
			return;
		}

		if (plugin.getGeneralConfig().useDeckItem()) {
			if (!plugin.getDeckManager().hasDeck(player, deckNumber)) {
				CardUtil.dropItem(player, plugin.getDeckManager().createDeck(player, deckNumber));
			} else {
				ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().alreadyHaveDeck());
			}
			return;
		}

		if (player.getGameMode() == GameMode.CREATIVE) {
			if (plugin.getGeneralConfig().deckInCreative()) {
				plugin.getDeckManager().openDeck(player, deckNumber);
				return;
			}
			ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().deckCreativeError());
			return;
		}

		plugin.getDeckManager().openDeck(player, deckNumber);
	}
}
