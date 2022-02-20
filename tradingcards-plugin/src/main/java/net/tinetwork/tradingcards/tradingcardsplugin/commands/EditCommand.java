package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
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
    public static class EditSubCommand extends BaseCommand {

        @Subcommand("card")
        @CommandPermission(Permissions.EDIT_CARD)
        public class EditCardSubCommand extends BaseCommand {
            @Subcommand("display-name")
            public void onEditDisplayName() {

            }
            @Subcommand("custom-model-data")
            public void onEditCustomModelData(){

            }
            @Subcommand("buy-price")
            public void onEditBuyPrice() {

            }
            @Subcommand("sell-price")
            public void onEditSellPrice(){

            }
            @Subcommand("info")
            public void onEditInfo() {

            }
            @Subcommand("series")
            public void onEditSeries(){


            }

            @Subcommand("type")
            public void onEditType(){

            }
        }

        @Subcommand("rarity")
        @CommandPermission(Permissions.EDIT_RARITY)
        public class EditRaritySubCommand extends BaseCommand {
            @Subcommand("display-name")
            public void onSetDisplayName(final CommandSender sender, final String displayName) {

            }
            @Subcommand("default-color")
            public void onSetDefaultColor(final CommandSender sender,final String defaultColor){

            }
            @Subcommand("buy-price")
            public void onSetBuyPrice(final CommandSender sender,final double buyPrice){

            }
            @Subcommand("sell-price")
            public void onSetSellPrice(final CommandSender sender, final double sellPrice){

            }
            @Subcommand("rewards add")
            @Description("adds a new entry to a rewards list.")
            public void onAddReward(final CommandSender sender, final String rewards) {

            }
            @Subcommand("rewards remove")
            @Description("Removes the last entry in a rewards list.")
            public void onRemoveReward(final CommandSender sender) {

            }
            @Subcommand("rewards remove-all")
            @Description("Removes all entries in a rewards list.")
            public void onRemoveAllRewards(final CommandSender sender) {

            }

        }


        @Subcommand("series")
        @CommandPermission(Permissions.EDIT_SERIES)
        public static class EditSeriesSubCommand extends BaseCommand {
            @Subcommand("display-name")
            public void onEditDisplayName() {

            }
            @Subcommand("mode")
            public void onEditMode(){

            }

            //series="" info="" etc
            @Subcommand("colors")
            @CommandCompletion("@series-colors")
            public void onEditColors(final CommandSender sender, String... colors){
                
            }

        }

        @Subcommand("pack")
        @CommandPermission(Permissions.EDIT_PACK)
        public static class EditPackSubCommand extends BaseCommand {
            @Subcommand("display-name")
            public void onEditDisplayName(){

            }
            @Subcommand("price")
            public void onEditPrice(){

            }
            @Subcommand("permission")
            public void onEditPermission(){

            }
        }

        @Subcommand("type")
        @CommandPermission(Permissions.EDIT_CUSTOM_TYPE)
        public static class EditTypeSubCommand extends BaseCommand {
            @Subcommand("type")
            @CommandCompletion("@default-types")
            public void onEditType(final CommandSender sender, final String type) {

            }
            @Subcommand("display-name")
            public void onEditDisplayName(final CommandSender sender, final String displayName) {

            }
        }
    }
}
