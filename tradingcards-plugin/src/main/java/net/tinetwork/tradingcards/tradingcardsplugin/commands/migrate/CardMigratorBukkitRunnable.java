package net.tinetwork.tradingcards.tradingcardsplugin.commands.migrate;

import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.local.YamlStorage;
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
        for(TradingCard card: yamlStorage.getCards()) {
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
    }
}
