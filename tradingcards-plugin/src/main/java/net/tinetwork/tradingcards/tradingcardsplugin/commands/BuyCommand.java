package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tinetwork.tradingcards.api.model.Pack;
import net.tinetwork.tradingcards.tradingcardsplugin.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class BuyCommand extends BaseCommand {
    private final TradingCards plugin;

    public BuyCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }
    @Subcommand("buy")
    @CommandPermission(Permissions.BUY)
    public class BuySubCommand extends BaseCommand {

        @Subcommand("pack")
        @CommandPermission(Permissions.BUY_PACK)
        @CommandCompletion("@packs")
        @Description("Buy a pack.")
        public void onBuyPack(final Player player, final String name) {
            if (!CardUtil.hasVault(player))
                return;
            if (plugin.getPackManager().getPack(name) == null) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().packDoesntExist());
                return;
            }

            Pack pack = plugin.getPackManager().getPack(name);

            if (pack.getBuyPrice() <= 0.0D) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().cannotBeBought());
                return;
            }

            EconomyResponse economyResponse = plugin.getEcon().withdrawPlayer(player, pack.getBuyPrice());
            if (economyResponse.transactionSuccess()) {
                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEcon().bankDeposit(plugin.getGeneralConfig().serverAccount(), pack.getBuyPrice());
                }
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().boughtCard().replace(PlaceholderUtil.AMOUNT, String.valueOf(pack.getBuyPrice())));
                CardUtil.dropItem(player, plugin.getPackManager().getPackItem(name));
                return;
            }

            ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().notEnoughMoney());
        }


        @Subcommand("card")
        @CommandPermission(Permissions.BUY_CARD)
        @Description("Buy a card.")
        @CommandCompletion("@rarities @cards")
        public void onBuyCard(final Player player, @NotNull final String rarity, @NotNull final String card) {
            if (!CardUtil.hasVault(player))
                return;

            if (plugin.getCardManager().getCard(card, rarity, false) instanceof EmptyCard) {
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().cardDoesntExist());
                return;
            }

            final TradingCard tradingCard = plugin.getCardManager().getCard(card, rarity, false);
            double buyPrice = tradingCard.getBuyPrice();

            EconomyResponse economyResponse = plugin.getEcon().withdrawPlayer(player, buyPrice);
            if (economyResponse.transactionSuccess()) {
                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEcon().bankDeposit(plugin.getGeneralConfig().serverAccount(), buyPrice);
                }
                CardUtil.dropItem(player, tradingCard.build(false));
                ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().boughtCard().replace(PlaceholderUtil.AMOUNT, String.valueOf(buyPrice)));
                return;
            }
            ChatUtil.sendPrefixedMessage(player, plugin.getMessagesConfig().notEnoughMoney());
        }
    }
}
