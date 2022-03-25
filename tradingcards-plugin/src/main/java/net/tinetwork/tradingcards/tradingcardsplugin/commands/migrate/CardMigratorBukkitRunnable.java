package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;

/**
 * @author sarhatabaot
 */
public class CardMigratorBukkitRunnable extends MigratorBukkitRunnable {
    @Override
    public String getMigrationType() {
        return "cards";
    }

    @Override
    public int getTotalAmount() {
        return source.getCards().size();
    }

    public CardMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender, final Storage<TradingCard> source) {
        super(plugin, sender, source);
    }

    @Override
    public void onExecute() {
        for(TradingCard card: source.getCards()) {
            Util.logAndMessage(sender,"Started conversion for "+card.getCardId());
            plugin.debug(CardMigratorBukkitRunnable.class,card.toString());
            final String cardId = card.getCardId();
            final String rarityId = card.getRarity().getName();
            final String seriesId = card.getSeries().getName();
            plugin.getStorage().createCard(cardId, rarityId,seriesId);
            plugin.getStorage().editCardBuyPrice(rarityId,cardId,seriesId,card.getBuyPrice());
            plugin.getStorage().editCardSellPrice(rarityId,cardId,seriesId,card.getSellPrice());
            plugin.getStorage().editCardInfo(rarityId,cardId ,seriesId,card.getInfo());
            plugin.getStorage().editCardCustomModelData(rarityId,cardId,seriesId,card.getCustomModelNbt());
            plugin.getStorage().editCardType(rarityId,cardId,seriesId,card.getType());
            plugin.getStorage().editCardDisplayName(rarityId,cardId,seriesId,card.getDisplayName());
            plugin.getStorage().editCardHasShiny(rarityId,cardId,seriesId,card.hasShiny());
        }
    }
}
