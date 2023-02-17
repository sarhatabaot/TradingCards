package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import net.tinetwork.tradingcards.api.economy.ResponseWrapper;
import net.tinetwork.tradingcards.api.model.pack.Pack;
import net.tinetwork.tradingcards.api.model.pack.PackEntry;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.EmptyCard;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.PlaceholderUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @CommandPermission(Permissions.User.Economy.BUY)
    public class BuySubCommand extends BaseCommand {

        @Subcommand("pack")
        @CommandPermission(Permissions.User.Economy.BUY_PACK)
        @CommandCompletion("@packs")
        @Description("Buy a pack.")
        public void onBuyPack(final Player player, final String packId) {
            if (CardUtil.noEconomy(player))
                return;

            if (plugin.getPackManager().getPack(packId) == null) {
                player.sendMessage(plugin.getMessagesConfig().packDoesntExist());
                return;
            }

            Pack pack = plugin.getPackManager().getPack(packId);

            if (pack.getBuyPrice() <= 0.0D && pack.getTradeCards().isEmpty()) {
                player.sendMessage(plugin.getMessagesConfig().cannotBeBought());
                return;
            }


            if (!pack.getTradeCards().isEmpty() && !CardUtil.hasCardsInInventory(player, pack.getTradeCards())) {
                player.sendMessage("Not enough cards to perform a trade in.");
                return;
            }

            ResponseWrapper economyResponse = plugin.getEconomyWrapper().withdraw(player, pack.getCurrencyId(), pack.getBuyPrice());
            if (economyResponse.success()) {

                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEconomyWrapper().depositAccount(plugin.getGeneralConfig().serverAccount(), pack.getCurrencyId(), pack.getBuyPrice());
                }

                if (!pack.getTradeCards().isEmpty()) {
                    Map<ItemStack, Integer> removedCardsMap = new HashMap<>();
                    for (PackEntry packEntry : pack.getTradeCards()) {
                        Map<ItemStack, Integer> removedEntryItems = CardUtil.removeCardsMatchingEntry(player, packEntry);

                        removedCardsMap = Stream.concat(removedCardsMap.entrySet().stream(), removedEntryItems.entrySet().stream()).collect(
                                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    }
                    final int totalCards = removedCardsMap.values().stream().mapToInt(Integer::intValue).sum();
                    player.sendMessage("Traded %s cards for pack %s:".formatted(totalCards,packId));
                    CardUtil.sendTradedCardsMessage(player, removedCardsMap);
                }

                player.sendMessage(plugin.getMessagesConfig().boughtCard().replaceAll(PlaceholderUtil.AMOUNT.asRegex(), String.valueOf(pack.getBuyPrice())));

                CardUtil.dropItem(player, plugin.getPackManager().getPackItem(packId));
                return;
            }

            player.sendMessage(plugin.getMessagesConfig().notEnoughMoney());
        }



        @Subcommand("card")
        @CommandPermission(Permissions.User.Economy.BUY_CARD)
        @Description("Buy a card.")
        @CommandCompletion("@rarities @cards")
        public void onBuyCard(final Player player, @NotNull final String rarityId, @NotNull final String cardId, @NotNull final String seriesId) {
            if (CardUtil.noEconomy(player))
                return;

            if (plugin.getCardManager().getCard(cardId, rarityId, seriesId) instanceof EmptyCard) {
                player.sendMessage(plugin.getMessagesConfig().cardDoesntExist());
                return;
            }

            final TradingCard tradingCard = plugin.getCardManager().getCard(cardId, rarityId, seriesId);
            final double buyPrice = tradingCard.getBuyPrice();
            final String currencyId = tradingCard.getCurrencyId();

            ResponseWrapper economyResponse = plugin.getEconomyWrapper().withdraw(player, currencyId, buyPrice);
            if (economyResponse.success()) {
                if (plugin.getGeneralConfig().closedEconomy()) {
                    plugin.getEconomyWrapper().depositAccount(plugin.getGeneralConfig().serverAccount(), currencyId, buyPrice);
                }
                CardUtil.dropItem(player, tradingCard.build(false));
                player.sendMessage(plugin.getMessagesConfig().boughtCard().replaceAll(PlaceholderUtil.AMOUNT.asRegex(), String.valueOf(buyPrice)));
                return;
            }
            player.sendMessage(plugin.getMessagesConfig().notEnoughMoney());
        }
    }
}
