package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.YamlStorage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.ConfigurateException;

/**
 * @author sarhatabaot
 */
public class CardMigratorBukkitRunnable extends MigratorBukkitRunnable {
    public CardMigratorBukkitRunnable(final TradingCards plugin, final CommandSender sender) {
        super(plugin, sender);
    }

    @Override
    public void onExecute() throws ConfigurateException {
        YamlStorage yamlStorage = new YamlStorage(plugin);
        yamlStorage.init(plugin);
        int cardsAmount = yamlStorage.getCards().size();
        Util.logAndMessage(sender,"Found "+cardsAmount+" cards.");
        for(TradingCard card: yamlStorage.getCards()) {
            Util.logAndMessage(sender,"Started conversion for "+card.getCardName());
            final String cardId = card.getCardName();
            final String rarityId = card.getRarity().getName();
            final String seriesId = card.getSeries().getName();
            plugin.getStorage().createCard(cardId, rarityId,seriesId);
            plugin.getStorage().editCardBuyPrice(rarityId,cardId,seriesId,card.getBuyPrice());
            plugin.getStorage().editCardSellPrice(rarityId,cardId,seriesId,card.getSellPrice());
            plugin.getStorage().editCardInfo(rarityId,cardId ,seriesId,card.getInfo());
            plugin.getStorage().editCardCustomModelData(rarityId,cardId,seriesId,card.getCustomModelNbt());
            plugin.getStorage().editCardType(rarityId,cardId,seriesId,card.getType());
            plugin.getStorage().editCardDisplayName(rarityId,cardId,seriesId,card.getDisplayName());
        }
        Util.logAndMessage(sender, "&2Finished conversion of " + cardsAmount + " cards.");
    }
}
