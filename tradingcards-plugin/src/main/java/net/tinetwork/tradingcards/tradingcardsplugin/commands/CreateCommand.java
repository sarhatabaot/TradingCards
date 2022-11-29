package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class CreateCommand extends BaseCommand{
    private final TradingCards plugin;

    public CreateCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("create")
    @CommandPermission(Permissions.CREATE)
    @Description("Creates any type, without customization, edit later using /cards edit.")
    public class CreateSubCommand extends BaseCommand {

        private void sendCreatedMessage(final @NotNull CommandSender sender, final String type, final String id) {
            sender.sendMessage(InternalMessages.CreateCommand.CREATED_TYPE.formatted(type,id));
            final String editId = id.replace("cardId:","").replace("rarityId:","").replace("seriesId:","");
            sender.sendMessage(InternalMessages.CreateCommand.CREATED_TYPE_EDIT.formatted(id,type,editId.replace(", ","")));
        }

        @Subcommand("rarity")
        @CommandPermission(Permissions.CREATE_RARITY)
        public void onRarity(final CommandSender sender,@Single final String rarityId) {
            if (plugin.getRarityManager().containsRarity(rarityId)) {
                sender.sendMessage(InternalMessages.CreateCommand.RARITY_EXISTS.formatted(rarityId));
                return;
            }

            sendCreatedMessage(sender, "rarity", rarityId);
            plugin.getStorage().createRarity(rarityId);
        }

        @Subcommand("card")
        @CommandPermission(Permissions.CREATE_CARD)
        @CommandCompletion("@nothing @rarities @series")
        public void onCard(final CommandSender sender,@Single final String cardId,@Single final String rarityId,@Single final String seriesId) {
            //Check if rarity & series exist
            if (plugin.getCardManager().containsCard(cardId, rarityId, seriesId)) {
                sender.sendMessage(InternalMessages.CreateCommand.CARD_EXISTS.formatted(cardId));
                return;
            }

            if(!plugin.getRarityManager().containsRarity(rarityId)) {
                sender.sendMessage(InternalMessages.NO_RARITY.formatted(rarityId));
                return;
            }

            if(!plugin.getSeriesManager().containsSeries(seriesId)) {
                sender.sendMessage(InternalMessages.NO_SERIES.formatted(seriesId));
                return;
            }

            final String createCardFormat = " cardId: %s, rarityId: %s, seriesId: %s".formatted(cardId,rarityId,seriesId);
            sendCreatedMessage(sender,"card",createCardFormat);
            plugin.getStorage().createCard(cardId, rarityId, seriesId);
        }

        @Subcommand("pack")
        @CommandPermission(Permissions.CREATE_PACK)
        public void onPack(final CommandSender sender,@Single final String packId) {
            if (plugin.getPackManager().containsPack(packId)) {
                sender.sendMessage(InternalMessages.CreateCommand.PACK_EXISTS.formatted(packId));
                return;
            }
            sendCreatedMessage(sender,"pack",packId);
            plugin.getStorage().createPack(packId);
        }

        @Subcommand("series")
        @CommandPermission(Permissions.CREATE_SERIES)
        public void onSeries(final CommandSender sender,@Single final String seriesId) {
            if (plugin.getSeriesManager().containsSeries(seriesId)) {
                sender.sendMessage(InternalMessages.CreateCommand.SERIES_EXISTS.formatted(seriesId));
                return;
            }
            sendCreatedMessage(sender,"series",seriesId);
            plugin.getStorage().createSeries(seriesId);
        }

        @Subcommand("type")
        @CommandPermission(Permissions.CREATE_CUSTOM_TYPE)
        @CommandCompletion("@nothing @default-types")
        public void onType(final CommandSender sender,@Single final String typeId,@Single final String type) {
            if (plugin.getDropTypeManager().containsType(typeId)) {
                sender.sendMessage(InternalMessages.CreateCommand.TYPE_EXISTS.formatted(typeId));
                return;
            }

            if (!plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList().contains(type)) {
                sender.sendMessage(InternalMessages.TYPE_MUST_BE.formatted(plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList().toString()));
                return;
            }
            sendCreatedMessage(sender,"customtype",typeId);
            plugin.getStorage().createCustomType(typeId, type);
        }

        @Subcommand("upgrade")
        @CommandCompletion("@nothing")
        @CommandPermission(Permissions.CREATE_UPGRADE)
        public void onUpgrade(final CommandSender sender, @Single final String upgradeId, @Single final PackEntry required, @Single final PackEntry result) {
            if (plugin.getUpgradeManager().containsUpgrade(upgradeId)) {
                sender.sendMessage(InternalMessages.CreateCommand.UPGRADE_EXISTS.formatted(upgradeId));
                return;
            }


            sendCreatedMessage(sender,"upgrade",upgradeId);
            plugin.getStorage().createUpgrade(upgradeId, required, result);
        }
    }
}
