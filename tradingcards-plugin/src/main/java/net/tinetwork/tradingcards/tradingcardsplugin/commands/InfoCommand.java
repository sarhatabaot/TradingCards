package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class InfoCommand extends BaseCommand {
    private final TradingCards plugin;

    public InfoCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("info")
    public class InfoSubCommand extends BaseCommand {

        @Subcommand("card")
        @CommandCompletion("@rarities @series @cards")
        public void onCard(final Player sender, final Rarity rarity, final Series series, final String cardId) {
            if (!plugin.getCardManager().containsCard(cardId, rarity.getId(), series.getId())) {
                sender.sendMessage(plugin.getMessagesConfig().noCard());
                return;
            }

            TradingCard card = plugin.getCardManager().getCard(cardId, rarity.getId(), series.getId());

            ChatUtil.sendPrefixedMessages(sender,
                    "Id: %s".formatted(card.getCardId()),
                    "Series: %s".formatted(card.getSeries().getId()),
                    "Rarity: %s".formatted(card.getRarity().getId()),
                    "Display Name: %s".formatted(card.getDisplayName()),
                    "Buy Price: %.2f".formatted(card.getBuyPrice()),
                    "Sell Price: %.2f".formatted(card.getSellPrice()),
                    "Currency Id: %s".formatted(card.getCurrencyId()),
                    "About: %s".formatted(card.getAbout()),
                    "Info: %s".formatted(card.getInfo()));
        }

        @Subcommand("pack")
        @CommandCompletion("@packs")
        public void onPack(final Player sender, final String packId) {
            if(!plugin.getPackManager().containsPack(packId)) {
                sender.sendMessage(plugin.getMessagesConfig().noBoosterPack());
                return;
            }

            final Pack pack = plugin.getPackManager().getPack(packId);
            ChatUtil.sendPrefixedMessages(sender,
                    "Id: %s".formatted(pack.getId()),
                    "Display Name: %s".formatted(pack.getDisplayName()),
                    "Content: %s".formatted(pack.getPackEntryList().stream().map(Pack.PackEntry::toString).toList()),
                    "Currency Id: %s".formatted(pack.getCurrencyId()),
                    "Buy Price: %s".formatted(pack.getBuyPrice())
                    );
        }

        @Subcommand("type")
        @CommandCompletion("@all-types")
        public void onType(final Player sender, final String typeId) {
            if(!plugin.getDropTypeManager().containsType(typeId)) {
                ChatUtil.sendPrefixedMessage(sender,"No type %s".formatted(typeId));
                return;
            }

            DropType type = plugin.getDropTypeManager().getType(typeId);
            ChatUtil.sendPrefixedMessages(sender,
                    "Id: %s".formatted(type.getId()),
                    "Display Name: %s".formatted(type.getDisplayName()),
                    "Type: %s".formatted(type.getType()));
        }

        @Subcommand("series")
        @CommandCompletion("@series")
        public void onSeries(final Player sender, final Series series) {
            ChatUtil.sendPrefixedMessages(sender,
                    "Id: %s".formatted(series.getId()),
                    "Display Name: %s".formatted(series.getDisplayName()),
                    "Mode: %s".formatted(series.getMode()),
                    "Colors: \n" +
                            "%s".formatted(series.getColorSeries().toString()));
        }

        @Subcommand("rarity")
        @CommandCompletion("@rarities")
        public void onRarity(final Player sender, final Rarity rarity) {
            ChatUtil.sendPrefixedMessages(sender,
                    "Id: %s".formatted(rarity.getId()),
                    "Display Name: %s".formatted(rarity.getDisplayName()),
                    "Default Color: %s".formatted(rarity.getDefaultColor()),
                    "Buy Price: %.2f".formatted(rarity.getBuyPrice()),
                    "Sell Price: %.2f".formatted(rarity.getSellPrice()),
                    "Currency Id: %s".formatted("")/*todo*/,
                    "Rewards:\n" +
                            "%s".formatted(rarity.getRewards()));
        }

        @Subcommand("mob")
        @Description("Display the mob group for this entity.")
        public void onMobInfo(final Player sender, final EntityType entityType) {
            final DropType dropType = plugin.getDropTypeManager().getMobType(entityType);
            ChatUtil.sendPrefixedMessage(sender, "Entity %s is %s".formatted(entityType.name(), dropType.getType()));
        }
    }
}
