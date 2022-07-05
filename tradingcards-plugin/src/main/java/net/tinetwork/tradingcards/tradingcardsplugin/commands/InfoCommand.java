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
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
        public void onCard(final Player sender, final @NotNull Rarity rarity, final @NotNull Series series, final String cardId) {
            if (!plugin.getCardManager().containsCard(cardId, rarity.getId(), series.getId())) {
                sender.sendMessage(plugin.getMessagesConfig().noCard());
                return;
            }

            TradingCard card = plugin.getCardManager().getCard(cardId, rarity.getId(), series.getId());

            ChatUtil.sendPrefixedMessages(sender,
                    InternalMessages.InfoCommand.CARD_FORMAT,
                    card.getCardId(),card.getSeries().getId(),card.getRarity().getId(),card.getDisplayName(),
                    card.getBuyPrice(),card.getSellPrice(),card.getCurrencyId(),card.getAbout(),card.getInfo());
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
                    InternalMessages.InfoCommand.PACK_FORMAT,
                    pack.getId(), pack.getDisplayName(),
                    pack.getPackEntryList().stream().map(Pack.PackEntry::toString).toList(),
                    pack.getCurrencyId(), pack.getBuyPrice());
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
                    InternalMessages.InfoCommand.TYPE_FORMAT,
                    type.getId(),type.getDisplayName(),type.getType());
        }

        @Subcommand("series")
        @CommandCompletion("@series")
        public void onSeries(final Player sender, final @NotNull Series series) {
            ChatUtil.sendPrefixedMessages(sender,
                    InternalMessages.InfoCommand.SERIES_FORMAT,
                    series.getId(), series.getDisplayName(), series.getMode(),
                    series.getColorSeries().toString());
        }

        @Subcommand("rarity")
        @CommandCompletion("@rarities")
        public void onRarity(final Player sender, final @NotNull Rarity rarity) {
            ChatUtil.sendPrefixedMessages(sender,
                    InternalMessages.InfoCommand.RARITY_FORMAT,
                    rarity.getId(),rarity.getDisplayName(), rarity.getDefaultColor(),
                    rarity.getBuyPrice(), rarity.getSellPrice(), "" /*todo*/, rarity.getRewards());
        }

        @Subcommand("mob")
        @Description("Display the mob group for this entity.")
        public void onMobInfo(final Player sender, final EntityType entityType) {
            final DropType dropType = plugin.getDropTypeManager().getMobType(entityType);
            ChatUtil.sendPrefixedMessage(sender, InternalMessages.InfoCommand.MOB_FORMAT.formatted(entityType.name(), dropType.getType()));
        }
    }
}
