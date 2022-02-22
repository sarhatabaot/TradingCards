package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import org.bukkit.command.CommandSender;

/**
 * @author sarhatabaot
 */

@CommandAlias("cards")
public class EditCommand extends BaseCommand {
    private final TradingCards plugin;

    public EditCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }

    @Subcommand("edit")
    @CommandPermission(Permissions.EDIT)
    @Description("Edit any value.")
    public class EditSubCommand extends BaseCommand {
        public enum EditCard {
            DISPLAY_NAME,
            CUSTOM_MODEL_DATA,
            BUY_PRICE,
            SELL_PRICE,
            INFO,
            SERIES,
            TYPE
        }
        @Subcommand("card")
        @CommandPermission(Permissions.EDIT_CARD)
        @CommandCompletion("@rarities @cards @edit-card @edit-card-value")
        public void onEditCard(final CommandSender sender, final String rarityId, final String cardId, final EditCard editCard,final String value) {

        }

        public enum EditRarity {
            DISPLAY_NAME,
            DEFAULT_COLOR,
            BUY_PRICE,
            SELL_PRICE,
            ADD_REWARD,
            REMOVE_REWARD,
            REMOVE_ALL_REWARDS
        }
        @Subcommand("rarity")
        @CommandPermission(Permissions.EDIT_RARITY)
        @CommandCompletion("@rarities @edit-rarity @edit-rarity-value")
        public void onEditRarity(final CommandSender sender, final String rarityId, final EditRarity editRarity, final String value) {

        }



        public enum EditSeries {
            DISPLAY_NAME,
            MODE,
            COLORS
        }
        @Subcommand("series")
        @CommandPermission(Permissions.EDIT_SERIES)
        @CommandCompletion("@series @edit-series @edit-series-value")
        //cards edit series <MODE|DISPLAY_NAME|COLORS> available-completion
        public void onEditSeries(final CommandSender sender, final String seriesId, final EditSeries editSeries, final String value) {

        }


        public enum EditPack {
            PRICE,
            PERMISSION,
            DISPLAY_NAME,
            CONTENTS;
        }

        //cards edit pack <packId> [displayName|price|permission|contents] (typeCompletion or nothing)
        @Subcommand("pack")
        @CommandCompletion("@packs @edit-pack @edit-pack-value") //Default Types needs to depend on edit-types
        @CommandPermission(Permissions.EDIT_PACK)
        public void onEditPack(final CommandSender sender, final String packId, final EditPack editType, final String value) {

        }


        public enum EditType {
            TYPE,
            DISPLAY_NAME;
        }

        //cards edit type <typeId> [type|displayName] (typeCompletion or nothing)
        @Subcommand("type")
        @CommandPermission(Permissions.EDIT_CUSTOM_TYPE)
        @CommandCompletion("@custom-types @edit-type @edit-type-value") //Default Types needs to depend on edit-types
        //If you want to add more than one word, quotations
        public void onEditType(final CommandSender sender, final String typeId, final EditType editType,@Single final String value) {

        }
    }

}
