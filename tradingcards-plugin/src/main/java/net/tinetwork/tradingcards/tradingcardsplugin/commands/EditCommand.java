package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.config.ColorSeries;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditUpgrade;
import net.tinetwork.tradingcards.tradingcardsplugin.managers.cards.CompositeCardKey;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.Edit;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditPack;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditRarity;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditSeries;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.LoggerUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class EditCommand extends BaseCommand {
    private final TradingCards plugin;
    private final Storage<TradingCard> storage;

    public EditCommand(final @NotNull TradingCards plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
    }

    @Subcommand("edit")
    @CommandPermission(Permissions.Admin.Edit.EDIT)
    @Description("Edit any value.")
    public class EditSubCommand extends BaseCommand {

        @Subcommand("card")
        @CommandPermission(Permissions.Admin.Edit.EDIT_CARD)
        @CommandCompletion("@rarities @series @command-cards @edit-card @edit-card-value")
        public void onEditCard(final CommandSender sender, final Rarity rarity, final Series series, final String cardId, final EditCard editCard, final String value) {
            final String seriesId = series.getId();
            final String rarityId = rarity.getId();
            if (!plugin.getRarityManager().containsRarity(rarityId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_RARITY.formatted(rarityId));
                return;
            }
            if (!plugin.getSeriesManager().containsSeries(seriesId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_SERIES.formatted(seriesId));
                return;
            }
            if (!plugin.getCardManager().containsCard(cardId, rarityId, seriesId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_CARD.formatted(StringUtils.join(List.of(cardId, rarityId, seriesId), "&r,&4 ")));
                return;
            }

            switch (editCard) {
                case DISPLAY_NAME -> storage.editCardDisplayName(rarityId, cardId, seriesId, value);
                case SELL_PRICE -> {
                    double sellPrice = getDoubleFromString(value);
                    if (sellPrice <= -1.00) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
                        return;
                    }
                    storage.editCardSellPrice(rarityId, cardId, seriesId, Double.parseDouble(value));
                }
                case TYPE -> {
                    if (!plugin.getDropTypeManager().containsType(value)) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_TYPE.formatted(value));
                        return;
                    }

                    final DropType type = plugin.getDropTypeManager().getType(value);
                    storage.editCardType(rarityId, cardId, seriesId, type);
                }
                case CUSTOM_MODEL_DATA -> {
                    int customModelData = getIntFromString(value);
                    if (customModelData <= 0) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.CUSTOM_MODEL_DATA_INCORRECT);
                        return;
                    }
                    storage.editCardCustomModelData(rarityId, cardId, seriesId, Integer.parseInt(value));
                }
                case INFO -> storage.editCardInfo(rarityId, cardId, seriesId, value);
                case SERIES -> {
                    if (!plugin.getSeriesManager().containsSeries(value)) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_SERIES.formatted(value));
                        return;
                    }
                    storage.editCardSeries(rarityId, cardId, seriesId, series);
                }
                case BUY_PRICE -> {
                    double buyPrice = getDoubleFromString(value);
                    if (buyPrice <= -1.00) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
                        return;
                    }
                    storage.editCardBuyPrice(rarityId, cardId, seriesId, Double.parseDouble(value));
                }
                case HAS_SHINY -> {
                    boolean hasShiny = Boolean.parseBoolean(value);
                    storage.editCardHasShiny(rarityId,cardId,seriesId,hasShiny);
                }
                case CURRENCY_ID -> storage.editCardCurrencyId(rarityId,cardId,seriesId, value);

            }

            plugin.getCardManager().getCache().refresh(new CompositeCardKey(rarityId,seriesId,cardId));
            String setCardMessage = "%s %s %s".formatted(cardId,rarityId,seriesId);
            sendSetTypes(sender, setCardMessage, editCard, value);
        }

        @Subcommand("rarity")
        @CommandPermission(Permissions.Admin.Edit.EDIT_RARITY)
        @CommandCompletion("@rarities @edit-rarity @edit-rarity-value")
        public void onEditRarity(final CommandSender sender, final String rarityId, final EditRarity editRarity, final String value) {
            if (!plugin.getRarityManager().containsRarity(rarityId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_RARITY.formatted(rarityId));
                return;
            }

            switch (editRarity) {
                case BUY_PRICE -> {
                    double buyPrice = getDoubleFromString(value);
                    if (buyPrice <= -1.00) {
                        ChatUtil.sendPrefixedMessage(sender,InternalMessages.EditCommand.PRICE_INCORRECT);
                        return;
                    }
                    storage.editRarityBuyPrice(rarityId, Double.parseDouble(value));

                }
                case ADD_REWARD -> storage.editRarityAddReward(rarityId, value);
                case DEFAULT_COLOR -> storage.editRarityDefaultColor(rarityId, value);
                case DISPLAY_NAME -> storage.editRarityDisplayName(rarityId, value);
                case SELL_PRICE -> {
                    double sellPrice = getDoubleFromString(value);
                    if (sellPrice <= -1.00) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
                        return;
                    }
                    storage.editRaritySellPrice(rarityId, Double.parseDouble(value));
                }
                case REMOVE_ALL_REWARDS -> storage.editRarityRemoveAllRewards(rarityId);
                case REMOVE_REWARD -> {
                    int rewardNumber = getIntFromString(value);
                    if (rewardNumber <= -1) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
                        return;
                    }
                    storage.editRarityRemoveReward(rarityId, Integer.parseInt(value));
                }
                case CUSTOM_ORDER -> storage.editRarityCustomOrder(rarityId,Integer.parseInt(value));

            }

            plugin.getRarityManager().getRarityCache().refresh(rarityId);
            sendSetTypes(sender, rarityId, editRarity, value);
        }


        @Subcommand("series")
        @CommandPermission(Permissions.Admin.Edit.EDIT_SERIES)
        @CommandCompletion("@series @edit-series @edit-series-value")
        //cards edit series <MODE|DISPLAY_NAME|COLORS> available-completion
        public void onEditSeries(final CommandSender sender, final String seriesId, final EditSeries editSeries, String value) {
            if (!plugin.getSeriesManager().containsSeries(seriesId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_SERIES.formatted(seriesId));
                return;
            }
            switch (editSeries) {
                case DISPLAY_NAME -> storage.editSeriesDisplayName(seriesId, value);
                case COLORS -> {
                    if (LoggerUtil.COLORS.stream().noneMatch(value::contains)) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.NO_COLORS_ARGS);
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.COLORS_HINT.formatted(LoggerUtil.COLORS));
                        return;
                    }

                    ColorSeries selectedSeriesColors = plugin.getSeriesManager().getSeries(seriesId).getColorSeries();
                    String info = selectedSeriesColors.getInfo();
                    String about = selectedSeriesColors.getAbout();
                    String type = selectedSeriesColors.getType();
                    String series = selectedSeriesColors.getSeries();
                    String rarity = selectedSeriesColors.getRarity();

                    List<String> colorOptions = List.of("info", "about", "type", "series", "rarity");
                    for(String string: value.split(" ")) {
                        final String option = string.split("=")[0];
                        if(colorOptions.contains(option)) {
                            switch (option) {
                                case "info" -> info = string.split("=")[1];
                                case "about" -> about = string.split("=")[1];
                                case "type" -> type = string.split("=")[1];
                                case "series" -> series = string.split("=")[1];
                                case "rarity" -> rarity = string.split("=")[1];
                                default -> plugin.debug(EditCommand.class, InternalDebug.EditCommand.UNSUPPORTED_ARG.formatted(value));
                            }
                        }
                    }


                    ColorSeries colorSeries = new ColorSeries(series, type, info, about, rarity);
                    storage.editSeriesColors(seriesId, colorSeries);
                }
                case MODE -> {
                    Mode mode = Mode.getMode(value);
                    if (mode == null) {
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.MODE_INCORRECT.formatted(Arrays.toString(Mode.values())));
                        return;
                    }
                    storage.editSeriesMode(seriesId, Mode.getMode(value));
                }
            }

            plugin.getSeriesManager().getCache().refresh(seriesId);
            sendSetTypes(sender, seriesId, editSeries, value);
        }

        private int checkCorrectPackEntryFormat(final CommandSender sender, final String value){
            if (!value.contains("=")) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.CONTENTS_SYNTAX);
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.CONTENTS_EXAMPLE);
                return -1;
            }

            String[] split = value.split("=");
            int lineNumber = getIntFromString(split[0]);
            if (lineNumber == -1) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.LINE_NUMBER_INCORRECT);
                return -1;
            }
            return lineNumber;
        }

        //cards edit pack <packId> [displayName|price|permission|contents] (typeCompletion or nothing)
        @Subcommand("pack")
        @CommandCompletion("@packs @edit-pack @edit-pack-value") //Default Types needs to depend on edit-types
        @CommandPermission(Permissions.Admin.Edit.EDIT_PACK)
        public void onEditPack(final CommandSender sender, final String packId, final EditPack editType, final String value) {
            if (!plugin.getStorage().containsPack(packId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_PACK.formatted(packId));
                return;
            }

            switch (editType) {
                case DISPLAY_NAME -> storage.editPackDisplayName(packId, value);
                case TRADE -> {
                    int lineNumber = checkCorrectPackEntryFormat(sender,value);
                    if(lineNumber == -1)
                        return;


                    String[] split = value.split("=");
                    String content = split[1];
                    if (content.equalsIgnoreCase("delete")) {
                        storage.editPackTradeCardsDelete(packId, lineNumber);
                        return;
                    }
                    
                    PackEntry entry = PackEntry.fromString(content);
                    storage.editPackTradeCards(packId, lineNumber, entry);
                }
                case CONTENTS -> {
                    int lineNumber = checkCorrectPackEntryFormat(sender,value);
                    if(lineNumber == -1)
                        return;


                    String[] split = value.split("=");
                    String content = split[1];
                    if (content.equalsIgnoreCase("delete")) {
                        storage.editPackContentsDelete(packId, lineNumber);
                        return;
                    }

                    PackEntry entry = PackEntry.fromString(content);
                    storage.editPackContents(packId, lineNumber, entry);
                }
                case PERMISSION -> storage.editPackPermission(packId, value);
                case PRICE -> {
                    double packPrice = getDoubleFromString(value);
                    if (packPrice <= -1.00) {
                        //There was a problem getting the price
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.EditCommand.PRICE_INCORRECT);
                        return;
                    }
                    storage.editPackPrice(packId, Double.parseDouble(value));
                }
                case CURRENCY_ID -> storage.editPackCurrencyId(packId, value);

            }

            plugin.getPackManager().getCache().refresh(packId);
            sendSetTypes(sender, packId, editType, value);
        }

        private double getDoubleFromString(final String string) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                return -1.00;
            }
        }

        private int getIntFromString(final String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        //cards edit type <typeId> [type|displayName] (typeCompletion or nothing)
        @Subcommand("type")
        @CommandPermission(Permissions.Admin.Edit.EDIT_CUSTOM_TYPE)
        @CommandCompletion("@custom-types @edit-type @edit-type-value") //Default Types needs to depend on edit-types
        //If you want to add more than one word, quotations
        public void onEditType(final CommandSender sender, final String typeId, final EditType editType, @Single final String value) {
            if (!plugin.getDropTypeManager().containsType(typeId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_TYPE.formatted(typeId));
                return;
            }

            switch (editType) {
                case DISPLAY_NAME -> storage.editCustomTypeDisplayName(typeId, value);
                case TYPE -> {
                    if (!plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList().contains(value)) {
                        //You must set type to one of the types
                        ChatUtil.sendPrefixedMessage(sender, InternalMessages.TYPE_MUST_BE.formatted(plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList().toString()));
                        return;
                    }

                    storage.editCustomTypeType(typeId, value);
                }
            }
            plugin.getDropTypeManager().getCache().refresh(typeId);
            sendSetTypes(sender, typeId, editType, value);
        }

        @Subcommand("upgrade")
        @CommandPermission(Permissions.Admin.Edit.EDIT_UPGRADE)
        @CommandCompletion("@upgrades @edit-upgrade @rarities @nothing @series")
        public void onEditUpgrade(final CommandSender sender, final String upgradeId, final EditUpgrade editUpgrade, final String rarityId, final int amount, final String seriesId) {
            if(!plugin.getUpgradeManager().containsUpgrade(upgradeId)) {
                ChatUtil.sendPrefixedMessage(sender, InternalMessages.NO_UPGRADE.formatted(upgradeId));
                return;
            }

            final PackEntry packEntry = new PackEntry(rarityId,amount,seriesId);
            switch (editUpgrade) {
                case RESULT -> storage.editUpgradeResult(upgradeId,packEntry);
                case REQUIRED -> storage.editUpgradeRequired(upgradeId,packEntry);
            }

            plugin.getUpgradeManager().getCache().refresh(upgradeId);
            String value = "%s %s %S".formatted(rarityId,amount,seriesId);
            sendSetTypes(sender,upgradeId,editUpgrade,value);

        }


        //set edit.toString() to value for id
        private void sendSetTypes(final CommandSender sender, final String id, final @NotNull Edit edit, final String value) {
            if ((edit instanceof EditRarity editRarity && editRarity == EditRarity.DEFAULT_COLOR) ||
                    (edit instanceof EditSeries editSeries && editSeries == EditSeries.COLORS)
            ) {
                String partialFormat = "&7Set &b%s &7to &b{value} &7for &b%s".formatted(edit, id);
                sender.sendMessage(ChatUtil.color(plugin.prefixed(partialFormat)).replace("{value}", value));
                return;
            }
            ChatUtil.sendPrefixedMessage(sender, "&7Set &b%s &7to &b%s &7for &b%s".formatted(edit, value, id));
        }
    }


}
