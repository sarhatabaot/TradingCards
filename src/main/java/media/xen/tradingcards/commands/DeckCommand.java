package media.xen.tradingcards.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import media.xen.tradingcards.CardUtil;
import media.xen.tradingcards.ChatUtil;
import media.xen.tradingcards.DeckManager;
import media.xen.tradingcards.TradingCards;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import static media.xen.tradingcards.TradingCards.sendMessage;

@CommandAlias("deck|decks")
public class DeckCommand extends BaseCommand {
	private TradingCards plugin;

	public DeckCommand(final TradingCards plugin) {
		this.plugin = plugin;
	}


	@Default
	@CommandPermission("cards.decks.get")
	@Description("Get a deck item. Or opens a deck.")
	public void onGetDeck(final Player player, final int deckNumber) {
		if (player.hasPermission("cards.decks." + deckNumber)) {
			if (plugin.getConfig().getBoolean("General.Use-Deck-Item")) {
				if (!plugin.hasDeck(player, deckNumber)) {
					CardUtil.dropItem(player,DeckManager.createDeck(player,deckNumber));
				} else {
					ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().alreadyHaveDeck);
				}
				return;
			}

			if (player.getGameMode() == GameMode.CREATIVE) {
				if (plugin.getMainConfig().decksInCreative) {
					DeckManager.openDeck(player, deckNumber);
					return;
				}
				ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().deckCreativeError);
				return;
			}
			DeckManager.openDeck(player, deckNumber);
			return;
		}

		sendMessage(player, plugin.getPrefixedMessage(plugin.getMessagesConfig().maxDecks));
	}
}
