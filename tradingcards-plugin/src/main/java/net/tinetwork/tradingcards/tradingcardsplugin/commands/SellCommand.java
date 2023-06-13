package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.tinetwork.tradingcards.api.economy.ResponseWrapper;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author sarhatabaot
 */
@CommandAlias("cards")
public class SellCommand extends BaseCommand {
    private final TradingCards plugin;

    public SellCommand(final TradingCards plugin) {
        this.plugin = plugin;
    }
    @Subcommand("sell")
    @CommandPermission(Permissions.User.Economy.SELL)
    public class SellSubCommand extends BaseCommand {
        @Default
        @Description("Sells the card in your main hand.")
        public void onSell(final Player player) {
            if (CardUtil.noEconomy(player))
                return;

            ItemStack itemInMainHand = player.getInventory().getItemInMainHand();
            if(itemInMainHand.getType() == Material.AIR || itemInMainHand.getType() == Material.CAVE_AIR || itemInMainHand.getType() == Material.VOID_AIR) {
                return;
            }
            
            final NBTItem nbtItem = new NBTItem(itemInMainHand);
            if (!CardUtil.isCard(nbtItem)) {
                ChatUtil.sendMessage(player, plugin.getMessagesConfig().notACard());
                return;
            }

            final ItemStack itemInHand = player.getInventory().getItemInMainHand();
            final int itemInHandSlot = player.getInventory().getHeldItemSlot();
            final String cardId = NbtUtils.Card.getCardId(nbtItem);
            final String rarityId = NbtUtils.Card.getRarityId(nbtItem);
            final String seriesId = NbtUtils.Card.getSeriesId(nbtItem);
            plugin.debug(SellSubCommand.class, InternalDebug.CardsCommand.CARD_RARITY_ID.formatted(cardId,rarityId));

            final TradingCard tradingCard = plugin.getCardManager().getCard(cardId, rarityId, seriesId);
            if (tradingCard.isShiny()) {
                ChatUtil.sendPrefixedMessage(player, InternalMessages.SellCommand.CANNOT_SELL_SHINY);
                return;
            }

            if (tradingCard.getSellPrice() <= 0.00D) {
                ChatUtil.sendPrefixedMessage(player, InternalMessages.CardsCommand.CANNOT_SELL);
                return;
            }

            PlayerInventory inventory = player.getInventory();
            double sellAmount = tradingCard.getSellPrice() * itemInHand.getAmount();
            ResponseWrapper economyResponse = plugin.getEconomyWrapper().deposit(player, tradingCard.getCurrencyId(),sellAmount);
            if (economyResponse.success()) {
                ChatUtil.sendPrefixedMessage(player, InternalMessages.SellCommand.SOLD_CARD.formatted(itemInHand.getAmount(), (rarityId + " " + cardId), sellAmount));
                inventory.setItem(itemInHandSlot, null);
            }
        }
    }
}
