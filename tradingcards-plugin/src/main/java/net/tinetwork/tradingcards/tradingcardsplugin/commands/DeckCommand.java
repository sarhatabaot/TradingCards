package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.TradingDeckManager;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.apache.commons.lang.Validate;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("deck")
public class DeckCommand extends BaseCommand {
	private final TradingCards plugin;
	private final TradingDeckManager deckManager;

	public DeckCommand(final TradingCards plugin) {
		this.plugin = plugin;
		this.deckManager = plugin.getDeckManager();
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
			dropDeckItem(player,deckNumber);
			return;
		}

		if(!plugin.getGeneralConfig().deckInCreative() && player.getGameMode() == GameMode.CREATIVE) {
			ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().deckCreativeError());
			return;
		}

		deckManager.openDeck(player, deckNumber);
	}

	private void dropDeckItem(final Player player, int deckNumber){
		if (!deckManager.hasDeckItem(player, deckNumber)) {
			CardUtil.dropItem(player, deckManager.getNbtItem(player, deckNumber));
		} else {
			ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().alreadyHaveDeck());
		}
	}
}
