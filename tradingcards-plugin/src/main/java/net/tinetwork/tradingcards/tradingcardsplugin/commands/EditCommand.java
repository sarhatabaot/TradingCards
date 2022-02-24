package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.model.DropType;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.api.model.schedule.Mode;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditCard;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditPack;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditRarity;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditSeries;
import net.tinetwork.tradingcards.tradingcardsplugin.commands.edit.EditType;
import net.tinetwork.tradingcards.tradingcardsplugin.storage.Storage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
//TODO
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
        @Subcommand("card")
        @CommandPermission(Permissions.EDIT_CARD)
        @CommandCompletion("@rarities @cards @series @edit-card @edit-card-value")
        public void onEditCard(final CommandSender sender, final String rarityId, final String cardId,final String seriesId, final EditCard editCard, final String value) {
            if(!plugin.getRarityManager().containsRarity(rarityId)) {
                //no such rarity
                return;
            }
            if(!plugin.getSeriesManager().containsSeries(seriesId)) {
                //no such series
                return;
            }
            if(!plugin.getCardManager().containsCard(cardId,rarityId,seriesId)) {
                //no such card
                return;
            }

            switch (editCard) {
                case DISPLAY_NAME -> storage.editCardDisplayName(rarityId,cardId,seriesId,value);
                case SELL_PRICE -> storage.editCardSellPrice(rarityId,cardId,seriesId, Double.parseDouble(value));
                case TYPE -> storage.editCardType(rarityId,cardId,seriesId,value);
                case CUSTOM_MODEL_DATA -> storage.editCardCustomModelData(rarityId,cardId,seriesId, Integer.parseInt(value));
                case INFO -> storage.editCardInfo(rarityId,cardId,seriesId,value);
                case SERIES -> storage.editCardSeries(rarityId,cardId,seriesId,value);
                case BUY_PRICE -> storage.editCardBuyPrice(rarityId,cardId,seriesId, Double.parseDouble(value));
            }

            //send set types
        }

        @Subcommand("rarity")
        @CommandPermission(Permissions.EDIT_RARITY)
        @CommandCompletion("@rarities @edit-rarity @edit-rarity-value")
        public void onEditRarity(final CommandSender sender, final String rarityId, final EditRarity editRarity, final String value) {
            if(!plugin.getRarityManager().containsRarity(rarityId)) {
                //no such rarity
                return;
            }

            //todo validate value
            switch (editRarity) {
                case BUY_PRICE -> storage.editRarityBuyPrice(rarityId, Double.parseDouble(value));
                case ADD_REWARD -> storage.editRarityAddReward(rarityId,value);
                case DEFAULT_COLOR -> storage.editRarityDefaultColor(rarityId,value);
                case DISPLAY_NAME -> storage.editRarityDisplayName(rarityId, value);
                case SELL_PRICE -> storage.editRaritySellPrice(rarityId, Double.parseDouble(value));
                case REMOVE_ALL_REWARDS -> storage.editRarityRemoveAllRewards(rarityId);
                case REMOVE_REWARD -> storage.editRarityRemoveReward(rarityId, Integer.parseInt(value));
            }

            //send set types
        }


        @Subcommand("series")
        @CommandPermission(Permissions.EDIT_SERIES)
        @CommandCompletion("@series @edit-series @edit-series-value")
        //cards edit series <MODE|DISPLAY_NAME|COLORS> available-completion
        public void onEditSeries(final CommandSender sender, final String seriesId, final EditSeries editSeries, final String value) {
            if(!plugin.getSeriesManager().containsSeries(seriesId)) {
                //no such series
                return;
            }

            switch (editSeries) {
                case DISPLAY_NAME -> storage.editSeriesDisplayName(seriesId,value);
                case COLORS -> storage.editSeriesColors(seriesId,value);
                case MODE -> storage.editSeriesMode(seriesId, Mode.valueOf(value));
            }

            //send set types
        }


        //cards edit pack <packId> [displayName|price|permission|contents] (typeCompletion or nothing)
        @Subcommand("pack")
        @CommandCompletion("@packs @edit-pack @edit-pack-value") //Default Types needs to depend on edit-types
        @CommandPermission(Permissions.EDIT_PACK)
        public void onEditPack(final CommandSender sender, final String packId, final EditPack editType, final String value) {
            if(!plugin.getPackManager().containsPack(packId)){
                //no such pack
                return;
            }

            switch (editType) {
                case DISPLAY_NAME -> storage.editPackDisplayName(packId,value);
                case CONTENTS -> {
                    if (value.contains("=")) {
                        String[] split = value.split("=");
                        int lineNumber = getLineNumber(split[0]);
                        if(lineNumber == -1){
                            //send message to sender that the line number is improper
                            return;
                        }
                        String content = split[1];
                        Pack.PackEntry entry = Pack.PackEntry.fromString(content);
                        storage.editPackContents(packId,lineNumber,entry);
                    }
                } //line=content i.e. 0=rarity:cardname:amount:series
                case PERMISSION -> storage.editPackPermission(packId,value);
                case PRICE -> storage.editPackPrice(packId, Double.parseDouble(value));
            }
            //send set types
        }

        private int getLineNumber(final String string) {
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException e){
                return -1;
            }
        }
        //cards edit type <typeId> [type|displayName] (typeCompletion or nothing)
        @Subcommand("type")
        @CommandPermission(Permissions.EDIT_CUSTOM_TYPE)
        @CommandCompletion("@custom-types @edit-type @edit-type-value") //Default Types needs to depend on edit-types
        //If you want to add more than one word, quotations
        public void onEditType(final CommandSender sender, final String typeId, final EditType editType, @Single final String value) {
            if(!plugin.getDropTypeManager().containsType(typeId)){
                //no such type
                return;
            }
            switch (editType) {
                case DISPLAY_NAME -> storage.editCustomTypeDisplayName(typeId,value);
                case TYPE -> {
                    if(!plugin.getDropTypeManager().getDefaultTypes().contains(value)) {
                        return;
                    }
                    final DropType type = plugin.getDropTypeManager().getType(value);
                    storage.editCustomTypeType(typeId,type);
                }
            }

            //send set types.
        }
    }

}
