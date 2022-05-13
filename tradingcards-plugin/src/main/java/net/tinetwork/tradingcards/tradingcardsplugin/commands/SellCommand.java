package net.tinetwork.tradingcards.tradingcardsplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import de.tr7zw.nbtapi.NBTItem;
import net.milkbowl.vault.economy.EconomyResponse;
import net.tinetwork.tradingcards.api.utils.NbtUtils;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalDebug;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.InternalMessages;
import net.tinetwork.tradingcards.tradingcardsplugin.messages.internal.Permissions;
import net.tinetwork.tradingcards.tradingcardsplugin.TradingCards;
import net.tinetwork.tradingcards.tradingcardsplugin.card.TradingCard;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.CardUtil;
import net.tinetwork.tradingcards.tradingcardsplugin.utils.ChatUtil;
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
    @CommandPermission(Permissions.SELL)
    public class SellSubCommand extends BaseCommand {
        @Default
        @Description("Sells the card in your main hand.")
        public void onSell(final Player player) {
            if (!CardUtil.hasVault(player))
                return;

            final NBTItem nbtItem = new NBTItem(player.getInventory().getItemInMainHand());
            if (!CardUtil.isCard(nbtItem)) {
                ChatUtil.sendMessage(player, plugin.getMessagesConfig().notACard());
                return;
            }

            final ItemStack itemInHand = player.getInventory().getItemInMainHand();
            final int itemInHandSlot = player.getInventory().getHeldItemSlot();
            final String cardId = nbtItem.getString(NbtUtils.NBT_CARD_NAME);
            final String rarityId = nbtItem.getString(NbtUtils.NBT_RARITY);
            final String seriesId = nbtItem.getString(NbtUtils.NBT_CARD_SERIES);
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
            EconomyResponse economyResponse = plugin.getEcon().depositPlayer(player, sellAmount);
            if (economyResponse.transactionSuccess()) {
                ChatUtil.sendPrefixedMessage(player, InternalMessages.SellCommand.SOLD_CARD.formatted(itemInHand.getAmount(), (rarityId + " " + cardId), sellAmount));
                inventory.setItem(itemInHandSlot, null);
            }
        }
    }
}
