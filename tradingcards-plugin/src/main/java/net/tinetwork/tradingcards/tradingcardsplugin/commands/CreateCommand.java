package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
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
            sender.sendMessage("Created " + type + " " + id);
            sender.sendMessage("To edit " + id + " run /cards edit " + type + " " + id);
        }

        @Subcommand("rarity")
        @CommandPermission(Permissions.CREATE_RARITY)
        public void onRarity(final CommandSender sender,@Single final String rarityId) {
            if (plugin.getRarityManager().containsRarity(rarityId)) {
                sender.sendMessage("This rarity already exists. Cannot create a new one.");
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
                sender.sendMessage("This card already exists. Cannot create a new one.");
                return;
            }

            if(!plugin.getRarityManager().containsRarity(rarityId)) {
                sender.sendMessage("This rarity doesn't exist.");
                return;
            }

            if(!plugin.getSeriesManager().containsSeries(seriesId)) {
                sender.sendMessage("This series doesn't exist.");
                return;
            }

            sendCreatedMessage(sender,"card","card-id: "+cardId +"rarity-id:"+ rarityId +"series-id" + seriesId);
            plugin.getStorage().createCard(cardId, rarityId, seriesId);
        }

        @Subcommand("pack")
        @CommandPermission(Permissions.CREATE_PACK)
        public void onPack(final CommandSender sender,@Single final String packId) {
            if (plugin.getPackManager().containsPack(packId)) {
                sender.sendMessage("This pack already exists. Cannot create a new one.");
                return;
            }
            sendCreatedMessage(sender,"pack",packId);
            plugin.getStorage().createPack(packId);
        }

        @Subcommand("series")
        @CommandPermission(Permissions.CREATE_SERIES)
        public void onSeries(final CommandSender sender,@Single final String seriesId) {
            if (plugin.getSeriesManager().containsSeries(seriesId)) {
                sender.sendMessage("This series already exists. Cannot create a new one.");
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
                sender.sendMessage("This type already exists. Cannot create a new one.");
                return;
            }

            if (!plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList().contains(type)) {
                sender.sendMessage("Type must be: all, hostile, neutral, passive or boss.");
                return;
            }
            sendCreatedMessage(sender,"customtype",typeId);
            plugin.getStorage().createCustomType(typeId, type);
        }
    }
}
