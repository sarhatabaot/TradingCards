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
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.Rarity;
import net.tinetwork.tradingcards.api.model.Series;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
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
import net.tinetwork.tradingcards.tradingcardsplugin.utils.Util;
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
    @CommandPermission(Permissions.EDIT)
    @Description("Edit any value.")
    public class EditSubCommand extends BaseCommand {
        private static final String SERIES_NOT_FOUND_FORMAT = "A series named &4%s&r could not be found.";
        private static final String NUMBER_NOT_FOUND_FORMAT = "Price must be higher than -1.";

        @Subcommand("card")
        @CommandPermission(Permissions.EDIT_CARD)
        @CommandCompletion("@rarities @series @command-cards @edit-card @edit-card-value")
        public void onEditCard(final CommandSender sender, final Rarity rarity, final Series series, final String cardId, final EditCard editCard, final String value) {
            final String seriesId = series.getId();
            final String rarityId = rarity.getId();
            if (!plugin.getRarityManager().containsRarity(rarityId)) {
                ChatUtil.sendPrefixedMessage(sender, String.format("A rarity named &4%s&r could not be found.", rarityId));
                return;
            }
            if (!plugin.getSeriesManager().containsSeries(seriesId)) {
                ChatUtil.sendPrefixedMessage(sender, String.format(SERIES_NOT_FOUND_FORMAT, seriesId));
                return;
            }
            if (!plugin.getCardManager().containsCard(cardId, rarityId, seriesId)) {
                ChatUtil.sendPrefixedMessage(sender, String.format("A card named &4%s&r could not be found.", StringUtils.join(List.of(cardId, rarityId, seriesId), "&r,&4 ")));
                return;
            }

            switch (editCard) {
                case DISPLAY_NAME -> storage.editCardDisplayName(rarityId, cardId, seriesId, value);
                case SELL_PRICE -> {
                    double sellPrice = getDoubleFromString(value);
                    if (sellPrice <= -1.00) {
                        ChatUtil.sendPrefixedMessage(sender, NUMBER_NOT_FOUND_FORMAT);
                        return;
                    }
                    storage.editCardSellPrice(rarityId, cardId, seriesId, Double.parseDouble(value));
                }
                case TYPE -> {
                    if (!plugin.getDropTypeManager().containsType(value)) {
                        ChatUtil.sendPrefixedMessage(sender, String.format("A type named &4%s&r could not be found.", value));
                        return;
                    }

                    final DropType type = plugin.getDropTypeManager().getType(value);
                    storage.editCardType(rarityId, cardId, seriesId, type);
                }
                case CUSTOM_MODEL_DATA -> {
                    int customModelData = getIntFromString(value);
                    if (customModelData <= 0) {
                        ChatUtil.sendPrefixedMessage(sender, "CustomModelData must be higher than 0.");
                        return;
                    }
                    storage.editCardCustomModelData(rarityId, cardId, seriesId, Integer.parseInt(value));
                }
                case INFO -> storage.editCardInfo(rarityId, cardId, seriesId, value);
                case SERIES -> {
                    if (!plugin.getSeriesManager().containsSeries(value)) {
                        ChatUtil.sendPrefixedMessage(sender, String.format(SERIES_NOT_FOUND_FORMAT, value));
                        return;
                    }
                    storage.editCardSeries(rarityId, cardId, seriesId, series);
                }
                case BUY_PRICE -> {
                    double buyPrice = getDoubleFromString(value);
                    if (buyPrice <= -1.00) {
                        ChatUtil.sendPrefixedMessage(sender, NUMBER_NOT_FOUND_FORMAT);
                        return;
                    }
                    storage.editCardBuyPrice(rarityId, cardId, seriesId, Double.parseDouble(value));
                }
            }


            String setCardMessage = String.format("%s %s %s", cardId,rarity,seriesId);
            sendSetTypes(sender, setCardMessage, editCard, value);
        }

        @Subcommand("rarity")
        @CommandPermission(Permissions.EDIT_RARITY)
        @CommandCompletion("@rarities @edit-rarity @edit-rarity-value")
        public void onEditRarity(final CommandSender sender, final String rarityId, final EditRarity editRarity, final String value) {
            if (!plugin.getRarityManager().containsRarity(rarityId)) {
                ChatUtil.sendPrefixedMessage(sender, String.format("A rarity named &4%s&r could not be found.", rarityId));
                return;
            }

            switch (editRarity) {
                case BUY_PRICE -> {
                    double buyPrice = getDoubleFromString(value);
                    if (buyPrice <= -1.00) {
                        ChatUtil.sendPrefixedMessage(sender, NUMBER_NOT_FOUND_FORMAT);
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
                        ChatUtil.sendPrefixedMessage(sender, NUMBER_NOT_FOUND_FORMAT);
                        return;
                    }
                    storage.editRaritySellPrice(rarityId, Double.parseDouble(value));
                }
                case REMOVE_ALL_REWARDS -> storage.editRarityRemoveAllRewards(rarityId);
                case REMOVE_REWARD -> {
                    int rewardNumber = getIntFromString(value);
                    if (rewardNumber <= -1) {
                        ChatUtil.sendPrefixedMessage(sender, NUMBER_NOT_FOUND_FORMAT);
                        return;
                    }
                    storage.editRarityRemoveReward(rarityId, Integer.parseInt(value));
                }
            }

            plugin.getRarityManager().getRarityCache().refresh(rarityId);
            sendSetTypes(sender, rarityId, editRarity, value);
        }


        @Subcommand("series")
        @CommandPermission(Permissions.EDIT_SERIES)
        @CommandCompletion("@series @edit-series @edit-series-value")
        //cards edit series <MODE|DISPLAY_NAME|COLORS> available-completion
        public void onEditSeries(final CommandSender sender, final String seriesId, final EditSeries editSeries, final String value) {
            if (!plugin.getSeriesManager().containsSeries(seriesId)) {
                ChatUtil.sendPrefixedMessage(sender, String.format(SERIES_NOT_FOUND_FORMAT, seriesId));
                return;
            }
            switch (editSeries) {
                case DISPLAY_NAME -> storage.editSeriesDisplayName(seriesId, value);
                case COLORS -> {
                    if (Util.COLORS.stream().noneMatch(value::contains)) {
                        ChatUtil.sendPrefixedMessage(sender, "Could not find any arguments for colors.");
                        ChatUtil.sendPrefixedMessage(sender, "Must have: " + Util.COLORS);
                        return;
                    }

                    ColorSeries selectedSeriesColors = plugin.getSeriesManager().getSeries(seriesId).getColorSeries();
                    String info = selectedSeriesColors.getInfo();
                    String about = selectedSeriesColors.getAbout();
                    String type = selectedSeriesColors.getType();
                    String series = selectedSeriesColors.getSeries();
                    String rarity = selectedSeriesColors.getRarity();
                    for (String string : value.split(" ")) {
                        switch (string) {
                            case "info=" -> info = string.split("=")[1];
                            case "about=" -> about = string.split("=")[1];
                            case "type=" -> type = string.split("=")[1];
                            case "series=" -> series = string.split("=")[1];
                            case "rarity" -> rarity = string.split("=")[1];
                            default -> plugin.debug(EditCommand.class, "Unsupported argument: " + value);
                        }
                    }

                    ColorSeries colorSeries = new ColorSeries(series, type, info, about, rarity);
                    storage.editSeriesColors(seriesId, colorSeries);
                }
                case MODE -> {
                    Mode mode = Mode.getMode(value);
                    if (mode == null) {
                        ChatUtil.sendPrefixedMessage(sender, "Mode must be one of " + Arrays.toString(Mode.values()));
                        return;
                    }
                    storage.editSeriesMode(seriesId, Mode.getMode(value));
                }
            }

            sendSetTypes(sender, seriesId, editSeries, value);
        }


        //cards edit pack <packId> [displayName|price|permission|contents] (typeCompletion or nothing)
        @Subcommand("pack")
        @CommandCompletion("@packs @edit-pack @edit-pack-value") //Default Types needs to depend on edit-types
        @CommandPermission(Permissions.EDIT_PACK)
        public void onEditPack(final CommandSender sender, final String packId, final EditPack editType, final String value) {
            if (!plugin.getPackManager().containsPack(packId)) {
                ChatUtil.sendPrefixedMessage(sender, String.format("A pack named &4%s&r could not be found.", packId));
                return;
            }

            switch (editType) {
                case DISPLAY_NAME -> storage.editPackDisplayName(packId, value);
                case CONTENTS -> {
                    final String syntax = "lineNumber=rarity:cardname:amount:series";
                    final String example = "0=common:zombie:1:default";
                    if (!value.contains("=")) {
                        //send incorrect format
                        ChatUtil.sendPrefixedMessage(sender, "Incorrect syntax use " + syntax);
                        ChatUtil.sendPrefixedMessage(sender, "For example: " + example);
                        return;
                    }

                    String[] split = value.split("=");
                    int lineNumber = getIntFromString(split[0]);
                    if (lineNumber == -1) {
                        //send message to sender that the line number is improper
                        ChatUtil.sendPrefixedMessage(sender, "Line number must be a number higher than -1");
                        return;
                    }
                    String content = split[1];
                    if (content.equalsIgnoreCase("delete")) {
                        storage.editPackContentsDelete(packId, lineNumber);
                        return;
                    }

                    Pack.PackEntry entry = Pack.PackEntry.fromString(content);
                    storage.editPackContents(packId, lineNumber, entry);

                }
                case PERMISSION -> storage.editPackPermission(packId, value);
                case PRICE -> {
                    double packPrice = getDoubleFromString(value);
                    if (packPrice <= -1.00) {
                        //There was a problem getting the price
                        ChatUtil.sendPrefixedMessage(sender, NUMBER_NOT_FOUND_FORMAT);
                        return;
                    }
                    storage.editPackPrice(packId, Double.parseDouble(value));
                }
            }

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
        @CommandPermission(Permissions.EDIT_CUSTOM_TYPE)
        @CommandCompletion("@custom-types @edit-type @edit-type-value") //Default Types needs to depend on edit-types
        //If you want to add more than one word, quotations
        public void onEditType(final CommandSender sender, final String typeId, final EditType editType, @Single final String value) {
            if (!plugin.getDropTypeManager().containsType(typeId)) {
                ChatUtil.sendPrefixedMessage(sender, String.format("A type named &4%s&r could not be found.", typeId));
                return;
            }

            switch (editType) {
                case DISPLAY_NAME -> storage.editCustomTypeDisplayName(typeId, value);
                case TYPE -> {
                    if (!plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList().contains(value)) {
                        //You must set type to one of the types
                        ChatUtil.sendPrefixedMessage(sender, "Type must be from " + plugin.getDropTypeManager().getDefaultTypes().stream().map(DropType::getId).toList().toString());
                        return;
                    }

                    storage.editCustomTypeType(typeId, value);
                }
            }
            sendSetTypes(sender, typeId, editType, value);
        }

        //set edit.toString() to value for id
        private void sendSetTypes(final CommandSender sender, final String id, final @NotNull Edit edit, final String value) {
            if (edit instanceof EditRarity editRarity && editRarity == EditRarity.DEFAULT_COLOR) {
                String partialFormat = String.format("&7Set &b%s &7to &b{value} &7for &b%s", edit, id);
                sender.sendMessage(ChatUtil.color(plugin.prefixed(partialFormat)).replace("{value}", value));
                return;
            }
            ChatUtil.sendPrefixedMessage(sender, String.format("&7Set &b%s &7to &b%s &7for &b%s", edit, value, id));
        }
    }


}
